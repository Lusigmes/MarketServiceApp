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

import portifolio.market_service.model.entity.Pagamento;
import portifolio.market_service.repository.PagamentoRepository;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {
    @Autowired
    PagamentoRepository pagamentoRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Pagamento>> findAll() {
        List<Pagamento> pagamentos = pagamentoRepository.findAll();
        return ResponseEntity.ok(pagamentos);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Pagamento> deleteById(@PathVariable(value = "id") long id) {
        pagamentoRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Pagamento> save(@RequestBody Pagamento Pagamento) {
        Pagamento savedPagamentos = pagamentoRepository.save(Pagamento);
        return new ResponseEntity<>(savedPagamentos, HttpStatus.CREATED);
    }
}
