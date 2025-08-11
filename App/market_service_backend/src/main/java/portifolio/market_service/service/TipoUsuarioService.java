package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portifolio.market_service.dto.ClienteResponseDTO;
import portifolio.market_service.dto.PrestadorResponseDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.PrestadorRepository;

@Service
public class TipoUsuarioService {
    //vou quebrar principio de responsabilidade unica aqui
    //vou tratar responsabilidades de cliente e prestador
    //era só criar um para cada, logo, to errando de proposito
    //depois separo as responsabilidades
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    PrestadorRepository prestadorRepository;
    @Autowired
    UsuarioService usuarioService;


    public List<ClienteResponseDTO> findAllClientes(){
        List<Cliente> clientes = clienteRepository.findAll();

        return clientes.stream()
            .map(cliente -> new ClienteResponseDTO(
                cliente.getId(),
                usuarioService.toDTO(cliente.getUsuario())
            )).toList();
    }

    public List<PrestadorResponseDTO> findAllPrestadores(){
        List<Prestador> prestadores = prestadorRepository.findAll();

        return prestadores.stream()
            .map(prestador -> new PrestadorResponseDTO(
                prestador.getId(),
                usuarioService.toDTO(prestador.getUsuario())
            )).toList();
    }


}
