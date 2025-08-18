package portifolio.market_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import portifolio.market_service.dto.LoginUsuarioDTO;

import portifolio.market_service.model.entity.Usuario;

import portifolio.market_service.repository.UsuarioRepository;

@Service
@AllArgsConstructor
public class UsuarioAuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authManager;

    public Usuario logarUsuario(LoginUsuarioDTO dto){
        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.email(), 
                dto.senha())
        );
        return usuarioRepository.findByEmailWithRelations(dto.email())
            .orElseThrow();
    }
    
}
