package portifolio.market_service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ChatConversationResponse(
    Long clienteId,
    String clienteNome,
    Long prestadorId,
    String prestadorNome,
    String ultimaMensagem,
    boolean enviadoPorCliente,
    boolean lida,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataUltimaMensagem
) {}