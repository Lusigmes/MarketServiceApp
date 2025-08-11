package portifolio.market_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import portifolio.market_service.dto.LoginTokenResponseDTO;
import portifolio.market_service.dto.LoginUsuarioDTO;
import portifolio.market_service.dto.RegistroUsuarioDTO;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.service.JwtAuthService;
import portifolio.market_service.service.UsuarioAuthService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UsuarioAuthService usuarioAuthService;
    private final JwtAuthService jwtService;


    @GetMapping("/me")
    public ResponseEntity<Usuario> usuarioAutenticado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(usuarioAtual);
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistroUsuarioDTO dto){
        Usuario usuarioRegistro = usuarioAuthService.registrarUsuario(dto);
        return ResponseEntity.ok(usuarioRegistro);
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponseDTO> logar(@RequestBody LoginUsuarioDTO dto){
        Usuario usuarioAuth = usuarioAuthService.logarUsuario(dto);

        String jwtToken = jwtService.generateToken(usuarioAuth);
        
        LoginTokenResponseDTO loginResponse = new LoginTokenResponseDTO()
            .setToken(jwtToken)
            .setExpiresIn(jwtService.getExpirationTime());
       
        return ResponseEntity.ok(loginResponse);
    }
}
