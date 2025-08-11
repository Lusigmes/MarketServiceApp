package portifolio.market_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import portifolio.market_service.dto.LoginUsuarioDTO;
import portifolio.market_service.dto.RegistroUsuarioDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.model.enums.RoleUsuario;
import portifolio.market_service.model.enums.TipoUsuario;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.PrestadorRepository;
import portifolio.market_service.repository.UsuarioRepository;

@Service
@AllArgsConstructor
public class UsuarioAuthService {

    @Autowired
    PrestadorRepository prestadorRepository;
    @Autowired
    ClienteRepository clienteRepository;

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;


public Usuario registrarUsuario(RegistroUsuarioDTO dto) {
    Usuario usuario = new Usuario();
    usuario.setNome(dto.nome());
    usuario.setCPF(Long.parseLong(dto.cpf()));
    usuario.setEmail(dto.email());
    usuario.setSenha(passwordEncoder.encode(dto.senha()));
    usuario.setCEP(Integer.parseInt(dto.cep()));
    usuario.setTipoUsuario(TipoUsuario.valueOf(dto.tipoUsuario().toUpperCase()));
    usuario.setRoleUsuario(RoleUsuario.USER);

    usuario = usuarioRepository.save(usuario);

    if (usuario.getTipoUsuario() == TipoUsuario.CLIENTE) {
        Cliente usuarioCliente = new Cliente();
        usuarioCliente.setUsuario(usuario);
        clienteRepository.save(usuarioCliente);
        // usuario.setCliente(usuarioCliente); // Opcional, para manter o objeto atualizado
    } else {
        Prestador usuarioPrestador = new Prestador();
        usuarioPrestador.setUsuario(usuario);
        prestadorRepository.save(usuarioPrestador);
        // usuario.setPrestador(usuarioPrestador); // Opcional
    }

    return usuario;
}


    public Usuario logarUsuario(LoginUsuarioDTO dto){
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.email(), 
                dto.senha())
        );
        return usuarioRepository.findByEmail(dto.email())
            .orElseThrow();
    }
    
}
