package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portifolio.market_service.dto.ClienteResponseDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.repository.ClienteRepository;

@Service
public class ClienteService {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ClienteRepository clienteRepository;


    public ClienteResponseDTO toDTO(Cliente cliente){
        return new ClienteResponseDTO(cliente.getId(),
        usuarioService.toDTO(cliente.getUsuario()));
    }
    
    public long findClienteIdByUsuarioId(long usuarioId){
        return clienteRepository.findClienteIdByUsuarioId(usuarioId);

        //id+usuario associado
        // Cliente cliente = clienteRepository.findClienteIdByUsuarioId(usuarioId);

        // return new ClienteResponseDTO(
        //     cliente.getId(), 
        //     usuarioService.toDTO(cliente.getUsuario())
        // );
    }


    public List<ClienteResponseDTO> findAllClientes(){
        return clienteRepository.findAllWithUsuarioAndRelationsClientes()
            .stream().map(this::toDTO).toList();
    }

}
