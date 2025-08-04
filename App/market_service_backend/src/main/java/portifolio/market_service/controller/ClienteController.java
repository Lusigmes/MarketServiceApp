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

import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.repository.ClienteRepository;


@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    ClienteRepository clienteRepository;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<List<Cliente>> findAll() {
        List<Cliente> clientes = clienteRepository.findAll();
        return ResponseEntity.ok(clientes);
    }

    @CrossOrigin
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Cliente> deleteById(@PathVariable(value = "id") long id) {
        clienteRepository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @CrossOrigin
    @PostMapping
    public ResponseEntity<Cliente> save(@RequestBody Cliente cliente) {
        Cliente savedClientes = clienteRepository.save(cliente);
        return new ResponseEntity<>(savedClientes, HttpStatus.CREATED);
    }
}
