    package portifolio.market_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import portifolio.market_service.model.enums.PrioridadeDemanda;
import portifolio.market_service.model.enums.StatusDemanda;

public record DemandaResponseDTO(
    Long id,
    String titulo,
    String descricao,
    String categoria,
    String localizacao,
    LocalDate prazo,
    BigDecimal orcamentoEstimado,
    PrioridadeDemanda prioridade,
    StatusDemanda statusDemanda,
    LocalDateTime dataCriacao,
    LocalDateTime ultimaAtualizacao,
    Long clienteId
) 
{}
