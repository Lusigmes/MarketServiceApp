package portifolio.market_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.AvaliacaoDTO;
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

}
