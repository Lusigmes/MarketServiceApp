package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.AvaliacaoDTO;
import portifolio.market_service.dto.AvaliacaoResponseDTO;
import portifolio.market_service.model.entity.Avaliacao;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.repository.AvaliacaoRepository;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.PrestadorRepository;

@Service
public class AvaliacaoService {
    
    @Autowired
    private AvaliacaoRepository avaliacaoRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
   
    @Autowired
    private PrestadorRepository prestadorRepository;

    @Transactional
    public Avaliacao salvar(AvaliacaoDTO dto){
        Cliente cliente = clienteRepository.findById(dto.clienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    
        Prestador prestador = prestadorRepository.findById(dto.prestadorId())
            .orElseThrow(() -> new EntityNotFoundException("Prestador não encontrado"));
    
    
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());
        avaliacao.setCliente(cliente);
        avaliacao.setPrestador(prestador);
        
        return  avaliacaoRepository.save(avaliacao);
    
    }

    @Transactional
    public AvaliacaoResponseDTO toDTO(Avaliacao avaliacao){
        return new AvaliacaoResponseDTO(
            avaliacao.getId(),
            avaliacao.getNota(),
            avaliacao.getComentario(),
            avaliacao.getClienteId(),
            avaliacao.getPrestadorId(),
            avaliacao.getDataAvaliacao()
            );  
    }

    public List<AvaliacaoResponseDTO> listToDTO( List<Avaliacao> avaliacoes){
        return avaliacoes.stream().map(this::toDTO).toList();
    }

        // criar atualização de avaliacao

}
