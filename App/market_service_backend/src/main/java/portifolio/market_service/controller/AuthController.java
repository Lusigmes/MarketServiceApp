package portifolio.market_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
import portifolio.market_service.dto.UsuarioResponseDTO;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.repository.UsuarioRepository;
import portifolio.market_service.service.JwtAuthService;
import portifolio.market_service.service.UsuarioAuthService;
import portifolio.market_service.service.UsuarioService;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UsuarioAuthService usuarioAuthService;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final JwtAuthService jwtService;


    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> usuarioAutenticado(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuarioAtual = (Usuario) auth.getPrincipal();
        if (usuarioAtual.getCliente() == null && usuarioAtual.getPrestador() == null) {
            usuarioAtual = usuarioRepository.findByEmailWithRelations(usuarioAtual.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        }
        return ResponseEntity.ok(usuarioService.toDTO(usuarioAtual));
    }

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistroUsuarioDTO dto){
        Usuario usuarioRegistro = usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok(usuarioRegistro);
    }
    

    @PostMapping("/login")
    public ResponseEntity<LoginTokenResponseDTO> logar(@RequestBody LoginUsuarioDTO dto){
        try{

            Usuario usuarioAuth = usuarioAuthService.logarUsuario(dto);
            
            String jwtToken = jwtService.generateToken(usuarioAuth);
            
            LoginTokenResponseDTO loginResponse = new LoginTokenResponseDTO()
            .setToken(jwtToken)
            .setExpiresIn(jwtService.getExpirationTime());
            
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
