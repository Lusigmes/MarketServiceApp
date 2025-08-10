package portifolio.market_service.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import portifolio.market_service.dto.LoginDTO;
import portifolio.market_service.dto.RegistroDTO;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.model.enums.RoleUsuario;
import portifolio.market_service.model.enums.TipoUsuario;
import portifolio.market_service.repository.UsuarioRepository;

@Service
@AllArgsConstructor
public class UsuarioAuthService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    public Usuario registrarUsuario(RegistroDTO dto){
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setCPF(Long.parseLong(dto.getCpf()));
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        usuario.setCEP(Integer.parseInt(dto.getCep()));
        usuario.setTipoUsuario(TipoUsuario.valueOf(dto.getTipoUsuario().toUpperCase()));
        usuario.setRoleUsuario(RoleUsuario.USER);

        return usuarioRepository.save(usuario);
    }

    public Usuario logarUsuario(LoginDTO dto){
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.getEmail(), 
                dto.getSenha())
        );
        return usuarioRepository.findByEmail(dto.getEmail())
            .orElseThrow();
    }
    
}
