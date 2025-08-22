package portifolio.market_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import portifolio.market_service.dto.LoginUsuarioDTO;
import portifolio.market_service.model.entity.Usuario;

@Service
@AllArgsConstructor
public class UsuarioAuthService {

    private final AuthenticationManager authManager;

    public Usuario logarUsuario(LoginUsuarioDTO dto){
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.email(), 
                dto.senha())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        return (Usuario) authentication.getPrincipal();
        // return usuarioRepository.findByEmailWithRelations(dto.email())
        //     .orElseThrow();
    }
    
}
