package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.DemandaDTO;
import portifolio.market_service.dto.DemandaResponseDTO;
import portifolio.market_service.dto.DemandaUpdateDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.model.enums.StatusDemanda;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.repository.PropostaRepository;

@Service
public class DemandaService {

    @Autowired
    private DemandaRepository demandaRepository;

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private PropostaRepository propostaRepository;

    @Transactional
    public Demanda salvar(DemandaDTO dto) {
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

        demanda.setStatusDemanda(dto.statusDemanda() != null ? dto.statusDemanda() : StatusDemanda.ABERTA);

        return demandaRepository.save(demanda);
    }

    public List<DemandaResponseDTO> listar() {
        return demandaRepository.findAll()
                .stream().map(this::responseToDTO)
                .toList();
    }

    public Page<DemandaResponseDTO> listarPaginado(Pageable pageable) {
        return demandaRepository.findAll(pageable)
                .map(this::responseToDTO);
    }

    public DemandaResponseDTO responseToDTO(Demanda demanda) {
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
                demanda.getClienteId(),
                demanda.getPropostaAceitaId()
        );
    }

    public Demanda atualizar(long id, DemandaUpdateDTO dto, long clienteId) {
        Demanda demanda = demandaRepository.findDemandaByIdWithClienteAndUsuario(id);

        if (demanda == null) {
            throw new EntityNotFoundException("Demanda não encontrada com id: " + id);
        }

        if (!demanda.getClienteId().equals(clienteId)) {
            throw new SecurityException("Você não tem permissão para atualizar esta demanda");
        }

        if (dto.titulo() != null) {
            demanda.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            demanda.setDescricao(dto.descricao());
        }
        if (dto.categoria() != null) {
            demanda.setCategoria(dto.categoria());
        }
        if (dto.localizacao() != null) {
            demanda.setLocalizacao(dto.localizacao());
        }
        if (dto.prazo() != null) {
            demanda.setPrazo(dto.prazo());
        }
        if (dto.orcamentoEstimado() != null) {
            demanda.setOrcamentoEstimado(dto.orcamentoEstimado());
        }

        if (dto.prioridade() != null) {
            demanda.setPrioridade(dto.prioridade());
        }

        if (dto.statusDemanda() != null && !dto.statusDemanda().equals(demanda.getStatusDemanda())) {
            regraStatusDemanda(demanda.getStatusDemanda(), dto.statusDemanda());
            demanda.setStatusDemanda(dto.statusDemanda());
        }

        if (dto.propostaAceitaId() != null) {
            Proposta propostaAceita = propostaRepository.findById(dto.propostaAceitaId())
                    .orElseThrow(() -> new EntityNotFoundException("Proposta não encontrada com id: " + dto.propostaAceitaId()));
            demanda.setPropostaAceita(propostaAceita);
        }

        return demandaRepository.save(demanda);
    }

    private void regraStatusDemanda(StatusDemanda atual, StatusDemanda novo) {
        if (atual == null) {
            if (novo != StatusDemanda.ABERTA) {
                throw new IllegalArgumentException("Nova demanda deve iniciar com status ABERTA");
            }
            return;
        }

        switch (atual) {
            case ABERTA -> {
                if (!(novo == StatusDemanda.EM_ANDAMENTO || novo == StatusDemanda.CANCELADA)) {
                    throw new IllegalArgumentException("Status ABERTA só pode ir para EM_ANDAMENTO ou CANCELADA");
                }
            }
            case EM_ANDAMENTO -> {
                if (!(novo == StatusDemanda.CONCLUIDA || novo == StatusDemanda.CANCELADA || novo == StatusDemanda.ABERTA)) {
                    throw new IllegalArgumentException("Status EM_ANDAMENTO só pode ir para CONCLUIDA, CANCELADA ou ABERTA");
                }
            }
            case CONCLUIDA, CANCELADA ->
                throw new IllegalArgumentException("Demanda já finalizada.");
        }
    }
}
