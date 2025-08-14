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

import jakarta.validation.Valid;
import portifolio.market_service.dto.DemandaDTO;
import portifolio.market_service.dto.DemandaResponseDTO;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.service.DemandaService;

@RestController
@RequestMapping("/demandas")
public class DemandaController {
    
    @Autowired
    private DemandaRepository demandaRepository;
    @Autowired
    private DemandaService demandaService;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<DemandaResponseDTO>> findAll() {
        return ResponseEntity.ok(demandaService.listar());
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Demanda> deleteById(@PathVariable(value = "id") long id) {
        demandaRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<DemandaResponseDTO> save(@Valid @RequestBody DemandaDTO demanda) {
        Demanda savedDemanda = demandaService.salvar(demanda);
        DemandaResponseDTO dto = demandaService.responseToDTO(savedDemanda);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    // criar atualização de demanda
}
