package portifolio.market_service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
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
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity< List<AvaliacaoResponseDTO>> findAll() {
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllAvalicaoWithRelations();
        List<AvaliacaoResponseDTO> dtos = avaliacaoService.listToDTO(avaliacoes);
        return ResponseEntity.ok(dtos);
        // return ResponseEntity.ok(avaliacaoRepository.findAllAvaliacaoDTOs());
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

    @GetMapping("/prestador/{prestadorId}/paginadas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<AvaliacaoResponseDTO>> findAvaliacoesDoPrestadorPaginado(
            @PathVariable Long prestadorId,
            Pageable pageable) {
        Page<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.findAvaliacoesDoPrestador(prestadorId, pageable);
        
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/cliente/{clienteId}/paginadas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<AvaliacaoResponseDTO>> findAvaliacoesDoClientePaginado(
            @PathVariable Long clienteId,
            Pageable pageable) {
        Page<AvaliacaoResponseDTO> avaliacoes = avaliacaoService.findAvaliacoesDoCliente(clienteId, pageable);
        
        return ResponseEntity.ok(avaliacoes);
    }
    
    @GetMapping("/prestador/{prestadorId}/estatisticas")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getEstatisticasPrestador(@PathVariable Long prestadorId) {
        Map<String, Object> estatisticas = avaliacaoService.obterMediasECountAvaliacoes(prestadorId);
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/verificar")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> verificarAvaliacaoExistente(
            @RequestParam Long clienteId,
            @RequestParam Long prestadorId,
            @RequestParam Long demandaId) {
        
        boolean existeAvaliacao = avaliacaoService.verificarAvaliacaoExistente(clienteId, prestadorId, demandaId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("existeAvaliacao", existeAvaliacao);
        response.put("clienteId", clienteId);
        response.put("prestadorId", prestadorId);
        response.put("demandaId", demandaId);
        
        return ResponseEntity.ok(response);
    }
}
