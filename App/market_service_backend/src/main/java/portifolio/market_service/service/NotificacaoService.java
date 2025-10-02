package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import portifolio.market_service.model.entity.Notificacao;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.repository.NotificacaoRepository;

@Service
public class NotificacaoService {
    
    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Transactional
    public void criarNotificacao(Usuario usuario, String mensagem){
        try {
            Notificacao n = new Notificacao();
            n.setUsuario(usuario);
            n.setMensagem(mensagem);
            n.setLida(false);
            notificacaoRepository.save(n);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Notificacao> listarNotificacaos(long usuarioId){
        return notificacaoRepository.findByUsuarioOrderByDataCriacaoNotificacaoDesc(usuarioId);
    }
    
    public void marcarComoLida(long notificacaoId){
        Notificacao n = notificacaoRepository.findById(notificacaoId)
            .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));

        n.setLida(true);
        notificacaoRepository.save(n);
        
    }

    public long contarNotificacoesNaoLidas(long usuarioId){
        return notificacaoRepository.countByUsuarioANdLidaFalse(usuarioId);
    }

}
