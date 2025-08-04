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

import portifolio.market_service.model.entity.Avaliacao;
import portifolio.market_service.repository.AvaliacaoRepository;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    @Autowired
    AvaliacaoRepository avaliacaoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Avaliacao>> findAll() {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAll();
        return ResponseEntity.ok(avaliacoes);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Avaliacao> deleteById(@PathVariable(value = "id") long id) {
        avaliacaoRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Avaliacao> save(@RequestBody Avaliacao avaliacao) {
        Avaliacao savedAvaliacoes = avaliacaoRepository.save(avaliacao);
        return new ResponseEntity<>(savedAvaliacoes, HttpStatus.CREATED);
    }
}
