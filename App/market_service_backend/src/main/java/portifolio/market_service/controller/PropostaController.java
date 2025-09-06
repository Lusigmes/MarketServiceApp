package portifolio.market_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import portifolio.market_service.dto.PropostaDTO;
import portifolio.market_service.dto.PropostaResponseDTO;
import portifolio.market_service.dto.PropostaUpdateDTO;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.repository.PropostaRepository;
import portifolio.market_service.service.PropostaService;

@RestController
@RequestMapping("/propostas")
public class PropostaController {
       
    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private PropostaService propostaService;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<Page<PropostaResponseDTO>> listarPropostasPaginado(
        @RequestParam Long demandaId, 
        Pageable pageable){   
        return ResponseEntity.ok(propostaService.listarPaginado(demandaId, pageable));
    }

    @CrossOrigin
    @GetMapping(value = "/doPrestador")
    public ResponseEntity<Page<PropostaResponseDTO>> listarPropostasdoPrestadorPaginado(
        @RequestParam Long prestadorId, 
        Pageable pageable){   
        return ResponseEntity.ok(propostaService.listarDoPrestadorPaginado(prestadorId, pageable));
    }
    
    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Proposta> deleteById(@PathVariable(value = "id") long id) {
        propostaRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<PropostaResponseDTO> save(@RequestBody PropostaDTO proposta) {
        Proposta savedPropostas = propostaService.salvar(proposta);
        PropostaResponseDTO dto = propostaService.responseToDTO(savedPropostas);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @CrossOrigin
    @PutMapping("/{id}")
    public ResponseEntity<PropostaResponseDTO> update(
        @PathVariable long id, 
        @Valid @RequestBody PropostaUpdateDTO dto
    ){
            
        Proposta updatedProposta = propostaService.atualizar(id, dto);
        
        return ResponseEntity.ok(propostaService.responseToDTO(updatedProposta));
    }
}
