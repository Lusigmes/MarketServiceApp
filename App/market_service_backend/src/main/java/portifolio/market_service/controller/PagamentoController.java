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
import portifolio.market_service.dto.PagamentoDTO;
import portifolio.market_service.dto.PagamentoResponseDTO;
import portifolio.market_service.model.entity.Pagamento;
import portifolio.market_service.repository.PagamentoRepository;
import portifolio.market_service.service.PagamentoService;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private PagamentoService pagamentoService;
    
    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
        List<PagamentoResponseDTO> pagamentoDTO = pagamentoService.listar();
        return ResponseEntity.ok(pagamentoDTO);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Pagamento> deleteById(@PathVariable(value = "id") long id) {
        pagamentoRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> save(@Valid @RequestBody PagamentoDTO pagamento) {
        Pagamento savedPagamentos = pagamentoService.salvar(pagamento);
        PagamentoResponseDTO dto = pagamentoService.responseToDTO(savedPagamentos);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }
}
