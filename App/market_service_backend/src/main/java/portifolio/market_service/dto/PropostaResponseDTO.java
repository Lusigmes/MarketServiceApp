package portifolio.market_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import portifolio.market_service.model.enums.StatusProposta;

public record PropostaResponseDTO(
    long id,
    String titulo,
    BigDecimal preco,
    String comentario,
    StatusProposta statusProposta,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataCriacao,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataAtualizacao,
    Long demandaId,
    Long prestadorId
) 
{   }
