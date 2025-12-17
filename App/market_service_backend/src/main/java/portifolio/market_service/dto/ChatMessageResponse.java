package portifolio.market_service.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import portifolio.market_service.model.enums.TipoChat;

public record ChatMessageResponse(
    Long id,
    TipoChat tipo,
    Long clienteId,
    String clienteNome, 
    Long prestadorId,
    String prestadorNome,
    Long demandaId,
    String demandaTitulo,
    String conteudo,
    boolean enviadoPorCliente,
    boolean lida,
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    LocalDateTime dataEnvio
) {
    
}
