package portifolio.market_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import portifolio.market_service.dto.NotificacaoResponseDTO;
import portifolio.market_service.model.entity.Notificacao;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.repository.NotificacaoRepository;

@Service
public class NotificacaoService {
    
    @Autowired
    private NotificacaoRepository notificacaoRepository;

    private static final Logger logger = LoggerFactory.getLogger(NotificacaoService.class);

    @Transactional
    public void criarNotificacao(Usuario usuario, String mensagem){
        try {
            if(usuario == null) {
                logger.warn("Tentativa de criar notificação para usuário nulo");
                return;
            }
            Notificacao notificacao = new Notificacao();
            notificacao.setUsuario(usuario);
            notificacao.setMensagem(mensagem);
            notificacao.setLida(false);
            notificacao.setDataCriacaoNotificacao(LocalDateTime.now());

            notificacaoRepository.save(notificacao);

            logger.info("Notificação criada para usuário {}: {}", usuario.getId(), mensagem);
      
        } catch (Exception e) {
            logger.error("Erro ao criar notificação para usuário: " + (usuario != null ? usuario.getId() : "null"), e);
        }
    }
    
    public List<NotificacaoResponseDTO> listarNotificacoes(long usuarioId){
        List<Notificacao> notificacoes = notificacaoRepository.findByUsuarioWithUsuarioOrderByDataCriacaoNotificacaoDesc(usuarioId);
        return notificacoes.stream()
                .map(NotificacaoResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public void marcarComoLida(long notificacaoId){
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId)
            .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        notificacao.setLida(true);

        notificacaoRepository.save(notificacao);
        
    }

    public long contarNotificacoesNaoLidas(long usuarioId){
        return notificacaoRepository.countByUsuarioANdLidaFalse(usuarioId);
    }

    // notificações especificas

    public void notificarPropostaEnviada(Usuario cliente, String nomePrestador, String tituloDemanda){
        String mensagem = String.format(
            "O prestador %s enviou uma proposta para sua demanda \"%s\".",
            nomePrestador, tituloDemanda
        );
        criarNotificacao(cliente, mensagem);
    }

    public void notificarPropostaAceita(Usuario prestador, String tituloDemanda){
        String mensagem = String.format(
            "Sua proposta para a demanda \"%s\" foi aceita!",
            tituloDemanda
        );
        criarNotificacao(prestador, mensagem);
    }

    public void notificarPropostaRecusada(Usuario prestador, String tituloDemanda){
        String mensagem = String.format(
            "Sua proposta para a demanda \"%s\" foi recusada.",
            tituloDemanda
        );
        criarNotificacao(prestador, mensagem);
    }

    public void notificarPropostaCanceladaPrestador(Usuario cliente, String nomePrestador, String tituloDemanda){
        String mensagem = String.format(
            "O prestador %s cancelou a proposta para sua demanda \"%s\".",
            nomePrestador, tituloDemanda
        );
        criarNotificacao(cliente, mensagem);
    }

    public void notificarDemandaConcluidaCliente(Usuario cliente, String tituloDemanda){
        String mensagem = String.format(
            "Sua demanda \"%s\" foi concluída com sucesso!",
            tituloDemanda
        );
        criarNotificacao(cliente, mensagem);
    }

    public void notificarPropostaConcluidaPrestador(Usuario cliente, String nomePrestador, String tituloDemanda){
        String mensagem = String.format(
            "O prestador %s concluiu o serviço da demanda \"%s\". " +
            "Por favor, confirme a conclusão para finalizar o pedido.",
            nomePrestador, tituloDemanda
        );
        criarNotificacao(cliente, mensagem);
    }

    public void notificarDemandaConcluidaPrestador(Usuario prestador, String tituloDemanda){
        String mensagem = String.format(
        "A demanda \"%s\" que você atendeu foi concluída!", tituloDemanda 
        );
        criarNotificacao(prestador, mensagem);
    }
    
    public void notificarDemandaCanceladaCliente(Usuario cliente, String tituloDemanda){
       String mensagem = String.format(
            "Sua demanda \"%s\" foi cancelada.",
            tituloDemanda
        );
        criarNotificacao(cliente, mensagem);
    }

    public void notificarDemandaCanceladaPrestador(Usuario prestador, String tituloDemanda){
        String mensagem = String.format(
            "A demanda \"%s\" que você estava atendendo foi cancelada.",
            tituloDemanda
        );
        criarNotificacao(prestador, mensagem);
    }

    public void notificarNovaDemanda(Usuario prestador, String tituloDemanda){
        String mensagem = String.format(
            "Nova demanda disponível: %s",
            tituloDemanda
        );
        criarNotificacao(prestador, mensagem);
    }


}
