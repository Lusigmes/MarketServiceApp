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

import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.repository.PropostaRepository;

@RestController
@RequestMapping("/propostas")
public class PropostaController {
       
    @Autowired
    PropostaRepository propostaRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Proposta>> findAll() {
        List<Proposta> propostas = propostaRepository.findAll();
        return ResponseEntity.ok(propostas);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Proposta> deleteById(@PathVariable(value = "id") long id) {
        propostaRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Proposta> save(@RequestBody Proposta proposta) {
        Proposta savedPropostas = propostaRepository.save(proposta);
        return new ResponseEntity<>(savedPropostas, HttpStatus.CREATED);
    }
}
