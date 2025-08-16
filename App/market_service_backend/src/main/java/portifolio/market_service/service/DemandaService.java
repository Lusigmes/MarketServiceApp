package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import portifolio.market_service.dto.DemandaDTO;
import portifolio.market_service.dto.DemandaResponseDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.DemandaRepository;

@Service
public class DemandaService {
    @Autowired
    private DemandaRepository demandaRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Transactional
    public Demanda salvar(DemandaDTO dto){
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        
        Demanda demanda = new Demanda();
        demanda.setTitulo(dto.titulo());
        demanda.setDescricao(dto.descricao());
        demanda.setCategoria(dto.categoria());
        demanda.setLocalizacao(dto.localizacao());    
        demanda.setPrazo(dto.prazo());
        demanda.setOrcamentoEstimado(dto.orcamentoEstimado());
        demanda.setPrioridade(dto.prioridade());
        demanda.setCliente(cliente);

        return demandaRepository.save(demanda);
    }


    public List<DemandaResponseDTO> listar(){
        return demandaRepository.findAll()
            .stream().map(this::responseToDTO)
            .toList();
    }

    public DemandaResponseDTO responseToDTO(Demanda demanda){
        return new DemandaResponseDTO(
            demanda.getId(),
            demanda.getTitulo(),
            demanda.getDescricao(),
            demanda.getCategoria(),
            demanda.getLocalizacao(),
            demanda.getPrazo(),
            demanda.getOrcamentoEstimado(),
            demanda.getPrioridade(),
            demanda.getStatusDemanda(),
            demanda.getDataCriacaoDemanda(),
            demanda.getUltimaAtualizacao(),
            demanda.getClienteId()
        );
    }
        // criar atualização de demanda

}
