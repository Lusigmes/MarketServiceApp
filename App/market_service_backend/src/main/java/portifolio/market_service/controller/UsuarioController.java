package portifolio.market_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.repository.UsuarioRepository;
import portifolio.market_service.service.UsuarioAuthService;



@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    UsuarioRepository usuarioRepository;
        
    // @PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Usuario>> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Usuario> deleteById(@PathVariable(value = "id") long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Usuario> save(@RequestBody Usuario usuario) {
        Usuario savedUsuarios = usuarioRepository.save(usuario);
        return new ResponseEntity<>(savedUsuarios, HttpStatus.CREATED);
    }



    // @CrossOrigin
    // @PutMapping("/{id}")
    // public ResponseEntity<Usuario> update(@PathVariable long id, @RequestBody Usuario usuario) {
    //     if (!usuarioRepository.existsById(id)) {
    //         return ResponseEntity.notFound().build();
    //     }
    //     usuario.setId(id); 
    //     Usuario usuarioAtualizado = usuarioRepository.save(usuario);
    //     return ResponseEntity.ok(usuarioAtualizado);
    // }
}
