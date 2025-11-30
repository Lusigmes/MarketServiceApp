package portifolio.market_service.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.AvaliacaoDTO;
import portifolio.market_service.dto.AvaliacaoResponseDTO;
import portifolio.market_service.model.entity.Avaliacao;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.repository.AvaliacaoRepository;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.PrestadorRepository;
import portifolio.market_service.repository.DemandaRepository;

@Service
public class AvaliacaoService {
    
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
   
    @Autowired
    private PrestadorRepository prestadorRepository;
    @Autowired
    private DemandaRepository demandaRepository;

    @Transactional
    public Avaliacao salvar(AvaliacaoDTO dto){
        Cliente cliente = clienteRepository.findById(dto.clienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    
        Prestador prestador = prestadorRepository.findById(dto.prestadorId())
            .orElseThrow(() -> new EntityNotFoundException("Prestador não encontrado"));
    
        Demanda demanda = demandaRepository.findDemandaById(dto.demandaId());
        if(demanda == null){
            throw new EntityNotFoundException("Demanda não encontrado");
        }
    
    
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());
        avaliacao.setCliente(cliente);
        avaliacao.setPrestador(prestador);
        avaliacao.setDemanda(demanda);
        
        Avaliacao savedAvaliacao = avaliacaoRepository.save(avaliacao);
    
        Avaliacao avaliacaoComRelacoes = avaliacaoRepository.findAvaliacaoByIdWithRelations(savedAvaliacao.getId());
        if (avaliacaoComRelacoes == null) {
            throw new EntityNotFoundException("Avaliação não encontrada após salvar");
        }

        return avaliacaoComRelacoes;
    }

    @Transactional
    public AvaliacaoResponseDTO toDTO(Avaliacao avaliacao){
        return new AvaliacaoResponseDTO(
            avaliacao.getId(),
            avaliacao.getNota(),
            avaliacao.getComentario(),
            avaliacao.getClienteId(),
            avaliacao.getPrestadorId(),
            avaliacao.getDataAvaliacao(),
            avaliacao.getNomeCliente(),
            avaliacao.getNomePrestador(),
            avaliacao.getDemandaId()
            );      
    }

    @Transactional
    public List<AvaliacaoResponseDTO> listToDTO( List<Avaliacao> avaliacoes){
        return avaliacoes.stream().map(this::toDTO).toList();
    }
    
    @Transactional
    public Page<AvaliacaoResponseDTO> findAvaliacoesDoPrestador(Long prestaddorId, Pageable pageable){
        prestadorRepository.findById(prestaddorId)
            .orElseThrow(() -> new EntityNotFoundException("Prestador não encontrado"));
        Page<Avaliacao> avaliacoes = avaliacaoRepository.findAvaliacaoByPrestadorId(prestaddorId, pageable);
        return avaliacoes.map(this::toDTO);
    }
    @Transactional
    public Page<AvaliacaoResponseDTO> findAvaliacoesDoCliente(Long clienteId, Pageable pageable){
        clienteRepository.findById(clienteId)
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        Page<Avaliacao> avaliacoes = avaliacaoRepository.findAvaliacaoByClienteId(clienteId, pageable);
        return avaliacoes.map(this::toDTO);
    }


    public Map<String,Object> obterMediasECountAvaliacoes(Long prestadorId){
        Double media = avaliacaoRepository.findMediaDasNotasDoPrestador(prestadorId);
        Long total = avaliacaoRepository.findContadorDeAvaliacoesDoPrestador(prestadorId);
        
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("media", media != null ? Math.round(media * 10.0) / 10.0 : 0.0);
        estatisticas.put("totalAvaliacoes", total != null ? total : 0);
        estatisticas.put("prestadorId", prestadorId);

        return estatisticas;
    }

    public boolean verificarAvaliacaoExistente(Long clienteId, Long prestadorId, Long demandaId) {
        return avaliacaoRepository.existeAvaliacaoEntreClientePrestadorDemanda(clienteId, prestadorId, demandaId);
    }

        // criar atualização de avaliacao

}
