package portifolio.market_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import portifolio.market_service.model.enums.PrioridadeDemanda;
import portifolio.market_service.model.enums.StatusDemanda;

public record DemandaUpdateDTO(
        String titulo,
        String descricao,
        String categoria,
        String localizacao,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate prazo,
        BigDecimal orcamentoEstimado,
        PrioridadeDemanda prioridade,
        StatusDemanda statusDemanda,
        Long propostaAceitaId
        ) {

}
