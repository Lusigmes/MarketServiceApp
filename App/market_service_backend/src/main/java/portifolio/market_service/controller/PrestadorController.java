package portifolio.market_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.repository.PrestadorRepository;

@RestController
@RequestMapping("/prestadores")
public class PrestadorController {

    @Autowired
    PrestadorRepository prestadorRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Prestador>> findAll() {
        List<Prestador> Prestadors = prestadorRepository.findAll();
        return ResponseEntity.ok(Prestadors);
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
}
