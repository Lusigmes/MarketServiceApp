package portifolio.market_service.dto;


import portifolio.market_service.model.entity.Notificacao;
import java.time.format.DateTimeFormatter;

public record NotificacaoResponseDTO(
    Long id,
    String mensagem,
    Boolean lida,
    String dataCriacaoNotificacao 
) {
    public static NotificacaoResponseDTO fromEntity(Notificacao notificacao) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataFormatada = notificacao.getDataCriacaoNotificacao().format(formatter);
        
        return new NotificacaoResponseDTO(
            notificacao.getId(),
            notificacao.getMensagem(),
            notificacao.getLida(),
            dataFormatada 
        );
    }
}