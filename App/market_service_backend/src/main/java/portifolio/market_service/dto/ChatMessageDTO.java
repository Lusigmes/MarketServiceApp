package portifolio.market_service.dto;

import portifolio.market_service.model.enums.TipoChat;

public record ChatMessageDTO(
    Long messageId,          
    TipoChat tipo,
    Long clienteId,
    Long prestadorId,
    String conteudo,
    boolean enviadoPorCliente,
    String destinatarioSessionId

) {
}
