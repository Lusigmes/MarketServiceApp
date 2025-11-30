package portifolio.market_service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record AvaliacaoResponseDTO(
    long id,
    int nota,
    String comentario,
    long clienteId,
    long prestadorId,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataAvaliacao,
    String nomeCliente,
    String nomePrestador,
    Long demandaId
) {}
