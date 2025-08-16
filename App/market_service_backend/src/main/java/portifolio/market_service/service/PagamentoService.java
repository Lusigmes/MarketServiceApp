package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.PagamentoDTO;
import portifolio.market_service.dto.PagamentoResponseDTO;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Pagamento;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.repository.PagamentoRepository;
import portifolio.market_service.repository.PropostaRepository;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository pagamentoRepository;
    @Autowired
    private DemandaRepository demandaRepository;
    @Autowired
    private PropostaRepository propostaRepository;


    public Pagamento salvar (PagamentoDTO dto){
        Demanda demanda = demandaRepository.findById(dto.demandaId())
            .orElseThrow(() -> new EntityNotFoundException("Demanda não encontrada"));

        Proposta proposta =  propostaRepository.findById(dto.propostaId())
            .orElseThrow(() -> new EntityNotFoundException("Proposta não encontrada"));        
        
        Pagamento pagamento = new Pagamento();
        pagamento.setValor(dto.valor());
        pagamento.setComissao(dto.comissao());
        pagamento.setStatusPagamento(dto.statusPagamento());
        pagamento.setFormaPagamento(dto.formaPagamento());
        pagamento.setDemanda(demanda);
        pagamento.setProposta(proposta);

        return pagamentoRepository.save(pagamento);

    }

    public List<PagamentoResponseDTO> listar(){
        return pagamentoRepository.findAll()
            .stream().map(this::responseToDTO)
            .toList();
    }

    public PagamentoResponseDTO responseToDTO(Pagamento pagamento){
        return new PagamentoResponseDTO(
            pagamento.getId(),
            pagamento.getValor(),
            pagamento.getComissao(),
            pagamento.getStatusPagamento(),
            pagamento.getFormaPagamento(),
            pagamento.getDataPagamento(),
            pagamento.getDataCriacaoPagamento(),
            pagamento.getUltimaAtualizacao(),
            pagamento.getDemandaId(),
            pagamento.getPropostaId()
        );
    }


    
}
