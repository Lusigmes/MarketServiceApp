package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.PropostaDTO;
import portifolio.market_service.dto.PropostaResponseDTO;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.repository.PrestadorRepository;
import portifolio.market_service.repository.PropostaRepository;

@Service
public class PropostaService {
    @Autowired
    private PropostaRepository propostaRepository;
    
    @Autowired
    private PrestadorRepository prestadorRepository;
    
    @Autowired
    private DemandaRepository demandaRepository;

    @Transactional
    public Proposta salvar(PropostaDTO dto){
        Demanda demanda = demandaRepository.findById(dto.demandaId())
            .orElseThrow(() -> new EntityNotFoundException("Demanda não encontrada"));
        Prestador prestador = prestadorRepository.findById(dto.prestadorId())
            .orElseThrow(() -> new EntityNotFoundException("Prestador não encontrado"));

        Proposta proposta = new Proposta();
        proposta.setTitulo(dto.titulo());
        proposta.setComentario(dto.comentario());
        proposta.setPreco(dto.preco());
        proposta.setStatusProposta(dto.statusProposta());
        proposta.setDemanda(demanda);
        proposta.setPrestador(prestador);

        return propostaRepository.save(proposta);
    }

    public List<PropostaResponseDTO> listar(){
        return propostaRepository.findAll()
            .stream().map(this::responseToDTO)
            .toList();
    }
    
    public PropostaResponseDTO responseToDTO(Proposta proposta){
        return new PropostaResponseDTO(
            proposta.getId(),
            proposta.getTitulo(),
            proposta.getPreco(),
            proposta.getComentario(),
            proposta.getStatusProposta(),
            proposta.getDataCriacaoProposta(),
            proposta.getUltimaAtualizacao(),
            proposta.getDemandaId(),
            proposta.getPrestadorId()
        );
    }
}
