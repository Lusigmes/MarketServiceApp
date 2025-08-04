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

import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.repository.DemandaRepository;

@RestController
@RequestMapping("/demandas")
public class DemandaController {
    
    @Autowired
    DemandaRepository demandaRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Demanda>> findAll() {
        List<Demanda> demandas = demandaRepository.findAll();
        return ResponseEntity.ok(demandas);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Demanda> deleteById(@PathVariable(value = "id") long id) {
        demandaRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Demanda> save(@RequestBody Demanda demanda) {
        Demanda savedDemandas = demandaRepository.save(demanda);
        return new ResponseEntity<>(savedDemandas, HttpStatus.CREATED);
    }
}
