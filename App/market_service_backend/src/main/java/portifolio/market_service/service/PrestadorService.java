package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
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
        return new PrestadorResponseDTO(
            prestador.getId(),
            usuarioService.toDTO(prestador.getUsuario()),
            prestador.getTelefone(),
            prestador.getEspecializacao()
        );
    }

    public List<PrestadorResponseDTO> findAllPrestadores(){
        return prestadorRepository.findAllWithUsuarioAndRelationPrestadors()
            .stream().map(this::toDTO).toList();
    }
  
    public long findClienteIdByUsuarioId(long usuarioId){
        return prestadorRepository.findPrestadorIdByUsuarioId(usuarioId);
    }

    public Page<PrestadorResponseDTO> findAllPrestadoresPaginado(Pageable paageable){
        Page<Prestador> prestadoresPage = prestadorRepository.findAllWithUsuario(paageable);
        return prestadoresPage.map(this::toDTO);
    }

    public PrestadorResponseDTO findPrestadorById(Long id) {
        Prestador prestador = prestadorRepository.findByIdWithUsuario(id);
        if(prestador == null){
            throw new EntityNotFoundException("Prestador não encontrado com ID: " + id);
        }
        return toDTO(prestador);
    }
}
