package portifolio.market_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import portifolio.market_service.model.enums.FormaPagamento;
import portifolio.market_service.model.enums.StatusPagamento;

public record PagamentoResponseDTO(
    long id,
    BigDecimal valor,
    BigDecimal comissao,    
    StatusPagamento statusPagamento,
    FormaPagamento formaPagamento, 
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataPagamento,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataCriacao,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime ultimaAtualizacao,
    long demandaId, 
    long propostaId
) {}
