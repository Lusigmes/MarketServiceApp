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

import portifolio.market_service.dto.AvaliacaoDTO;
import portifolio.market_service.dto.AvaliacaoResponseDTO;
import portifolio.market_service.model.entity.Avaliacao;
import portifolio.market_service.repository.AvaliacaoRepository;
import portifolio.market_service.service.AvaliacaoService;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    
    @Autowired
    private AvaliacaoService avaliacaoService;
    
    @CrossOrigin
    @GetMapping
    public ResponseEntity< List<AvaliacaoResponseDTO>> findAll() {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllAvalicaoWithRelations();
        List<AvaliacaoResponseDTO> dtos = avaliacaoService.listToDTO(avaliacoes);
       
        return ResponseEntity.ok(dtos);
    }


    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Avaliacao> deleteById(@PathVariable(value = "id") long id) {
        avaliacaoRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> save(@RequestBody AvaliacaoDTO avaliacao) {
        Avaliacao savedAvaliacoes = avaliacaoService.salvar(avaliacao);
        AvaliacaoResponseDTO dto = avaliacaoService.toDTO(savedAvaliacoes);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    // criar atualização de avaliacao

}
