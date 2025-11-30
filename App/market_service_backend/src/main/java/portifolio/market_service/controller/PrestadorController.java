package portifolio.market_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import portifolio.market_service.dto.PrestadorResponseDTO;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.repository.PrestadorRepository;
import portifolio.market_service.service.PrestadorService;

@RestController
@RequestMapping("/prestadores")
public class PrestadorController {

    @Autowired
    private PrestadorRepository prestadorRepository;
    @Autowired
    private PrestadorService prestadorService;
    
    @CrossOrigin
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<PrestadorResponseDTO>> findAll() {
        List<PrestadorResponseDTO> prestadoresDTO = prestadorService.findAllPrestadores();
        return ResponseEntity.ok(prestadoresDTO);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Prestador> deleteById(@PathVariable(value = "id") long id) {
        prestadorRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Prestador> save(@RequestBody Prestador prestador) {
        Prestador savedPrestadors = prestadorRepository.save(prestador);
        return new ResponseEntity<>(savedPrestadors, HttpStatus.CREATED);
    }

    @CrossOrigin
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Long> findClienteIdByUsuarioId(@PathVariable("usuarioId") long usuarioId){
        return ResponseEntity.ok(prestadorService.findClienteIdByUsuarioId(usuarioId));
    }
    @CrossOrigin
    @GetMapping("/paginados")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<PrestadorResponseDTO>> listarPrestadoresPaginado(Pageable pageable){
        Page<PrestadorResponseDTO> prestadorePage = prestadorService.findAllPrestadoresPaginado(pageable);
        return ResponseEntity.ok(prestadorePage);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<PrestadorResponseDTO> findById(@PathVariable Long id) {
        PrestadorResponseDTO prestador = prestadorService.findPrestadorById(id);
        return ResponseEntity.ok(prestador);
    }

}
