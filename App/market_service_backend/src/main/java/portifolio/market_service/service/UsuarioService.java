package portifolio.market_service.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import portifolio.market_service.dto.RegistroUsuarioDTO;
import portifolio.market_service.dto.UsuarioResponseDTO;
import portifolio.market_service.dto.UsuarioUpdateDTO;
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
public class UsuarioService {
    
    private final PrestadorRepository prestadorRepository;

    private final ClienteRepository clienteRepository;

    private final UsuarioRepository usuarioRepository;
    
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public Usuario registrarUsuario(RegistroUsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setCPF(Long.parseLong(dto.cpf()));
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setCEP(dto.cep());
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
            usuarioPrestador.setTelefone(dto.telefone());
            usuarioPrestador.setEspecializacao(dto.especializacao());
            prestadorRepository.save(usuarioPrestador);
            // usuario.setPrestador(usuarioPrestador); // Opcional
        }

        return usuario;
    }

    // para cadastro
    public UsuarioResponseDTO toDTO(Usuario usuario){
        String telefone = null;
        String especializacao = null;

        if(usuario.getPrestador() != null){
            telefone = usuario.getPrestador().getTelefone();
            especializacao = usuario.getPrestador().getEspecializacao();
        }
   
        return new UsuarioResponseDTO(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            usuario.getCEP(),
            usuario.getTipoUsuario().name(),
            usuario.getRoleUsuario().name(),
            telefone, especializacao
        );
    }
    @Transactional
    public Usuario atualizar(long id, UsuarioUpdateDTO dto){
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
   
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setCEP(dto.cep());

        if(usuario.getPrestador() != null){
            Prestador usuarioPrestador = usuario.getPrestador();
            usuarioPrestador.setTelefone(dto.telefone());
            usuarioPrestador.setEspecializacao(dto.especializacao());
            prestadorRepository.save(usuarioPrestador);
        }
   
        return usuarioRepository.save(usuario);
    }
}
