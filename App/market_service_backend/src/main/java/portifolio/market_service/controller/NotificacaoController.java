package portifolio.market_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import portifolio.market_service.dto.NotificacaoResponseDTO;
import portifolio.market_service.service.NotificacaoService;

@RestController
@RequestMapping("notificacoes")
public class NotificacaoController {
   
    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/{usuarioId}")
    public List<NotificacaoResponseDTO> listar(@PathVariable long usuarioId){
        return notificacaoService.listarNotificacoes(usuarioId);
    }
    
    @PostMapping("/{id}/lida")
    public void marcarComoLida(@PathVariable long id){
        notificacaoService.marcarComoLida(id);
    }
    
    @GetMapping("/{usuarioId}/count")
    public long contarNaoLidas(@PathVariable long usuarioId){
        return notificacaoService.contarNotificacoesNaoLidas(usuarioId);
    }

}
