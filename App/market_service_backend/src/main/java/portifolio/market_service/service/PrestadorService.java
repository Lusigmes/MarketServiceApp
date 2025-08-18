package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import portifolio.market_service.dto.PrestadorResponseDTO;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.repository.PrestadorRepository;

@Service
public class PrestadorService {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private PrestadorRepository prestadorRepository;


    public PrestadorResponseDTO toDTO(Prestador prestador){
        return new PrestadorResponseDTO(prestador.getId(),
        usuarioService.toDTO(prestador.getUsuario()));
    }

    public List<PrestadorResponseDTO> findAllPrestadores(){


        return prestadorRepository.findAllWithUsuarioAndRelationPrestadors()
            .stream().map(this::toDTO).toList();
    }

}
