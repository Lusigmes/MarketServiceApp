package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.PropostaDTO;
import portifolio.market_service.dto.PropostaResponseDTO;
import portifolio.market_service.dto.PropostaUpdateDTO;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.model.enums.StatusProposta;
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
    public Proposta salvar(PropostaDTO dto) {
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

    public List<PropostaResponseDTO> listar() {
        return propostaRepository.findAll()
                .stream().map(this::responseToDTO)
                .toList();
    }

    public Page<PropostaResponseDTO> listarPaginado(Long demandaId, Pageable pageable) {
        return propostaRepository.findByDemanda_Id(demandaId, pageable)
                .map(this::responseToDTO);
    }

    public PropostaResponseDTO responseToDTO(Proposta proposta) {
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

    public Proposta atualizar(long id, PropostaUpdateDTO dto) {
        Proposta proposta = propostaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Proposta não encontrada"));

        if (dto.titulo() != null) {
            proposta.setTitulo(dto.titulo());
        }
        if (dto.preco() != null) {
            proposta.setPreco(dto.preco());
        }
        if (dto.comentario() != null) {
            proposta.setComentario(dto.comentario());
        }

        if (dto.statusProposta() != null && dto.statusProposta() != proposta.getStatusProposta()) {

            if (dto.statusProposta() == StatusProposta.ACEITA) {
                Long demandaId = proposta.getDemanda().getId(); // pega demanda da proposta
                boolean existeAceita = propostaRepository
                        .existsByDemanda_IdAndStatusProposta(demandaId, StatusProposta.ACEITA);

                if (existeAceita) {
                    throw new IllegalStateException("Já existe uma proposta ACEITA para esta demanda. "
                            + "Desfaça a aceitação anterior antes de aceitar outra proposta.");
                }
            }
            regraStatusProposta(proposta.getStatusProposta(), dto.statusProposta());
            proposta.setStatusProposta(dto.statusProposta());
        }

        return propostaRepository.save(proposta);
    }

    private void regraStatusProposta(StatusProposta atual, StatusProposta novo) {
        if (atual == null) {
            if (novo != StatusProposta.PENDENTE) {
                throw new IllegalArgumentException("Nova proposta deve iniciar com status PENDENTE");
            }
            return;
        }

        switch (atual) {
            case PENDENTE -> {
                if (!(novo == StatusProposta.ACEITA || novo == StatusProposta.RECUSADA)) {
                    throw new IllegalArgumentException(
                            "Proposta PENDENTE só pode ir para ACEITA ou RECUSADA"
                    );
                }
            }
            case ACEITA -> {
                if (!(novo == StatusProposta.CONCLUIDA || novo == StatusProposta.CANCELADA || novo == StatusProposta.PENDENTE)) {
                    throw new IllegalArgumentException(
                            "Proposta ACEITA só pode ir para CONCLUIDA ou CANCELADA"
                    );
                }
            }
            case RECUSADA -> {
                if (novo != StatusProposta.PENDENTE) {
                    throw new IllegalArgumentException(
                            "Proposta RECUSADA só pode voltar para PENDENTE"
                    );
                }
            }
            case CONCLUIDA -> {
                if (novo != StatusProposta.ACEITA) {
                    throw new IllegalArgumentException(
                            "Proposta CONCLUIDA só pode voltar para ACEITA"
                    );
                }
            }
            case CANCELADA -> {
                if (novo != StatusProposta.PENDENTE) {
                    throw new IllegalArgumentException(
                            "Proposta CANCELADA só pode voltar para PENDENTE"
                    );
                }
            }
        }
    }
}
