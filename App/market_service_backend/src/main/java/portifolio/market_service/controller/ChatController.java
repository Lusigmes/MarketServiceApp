package portifolio.market_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import portifolio.market_service.dto.ChatConversationResponse;
import portifolio.market_service.dto.ChatMessageDTO;
import portifolio.market_service.dto.ChatMessageResponse;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.service.ChatService;


@RequestMapping("chats")
@RestController
public class ChatController {
    @Autowired
    private ChatService chatService;
 
        
    @PostMapping
    public ResponseEntity<ChatMessageResponse> enviarMensagem(@RequestBody ChatMessageDTO chatDTO, @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(chatService.save(chatDTO, usuario));
    }


    @GetMapping("/historico")
    public ResponseEntity<List<ChatMessageResponse>> getHistorico(
            @RequestParam Long clienteId, @RequestParam Long prestadorId) {
        List<ChatMessageResponse> historico = chatService.getChatHistory(clienteId, prestadorId);
        return ResponseEntity.ok(historico);
    }

    @GetMapping("/mensagem/{msgId}")
    public ResponseEntity<ChatMessageResponse> getMensagem(@PathVariable Long msgId) {
        ChatMessageResponse mensagem = chatService.getMessage(msgId);
        return ResponseEntity.ok(mensagem);
    }

    @PostMapping("/{msgId}/ler")
    public ResponseEntity<ChatMessageResponse> marcarComoLida(@PathVariable Long msgId) {
        ChatMessageResponse mensagemAtualizada = chatService.markMessageAsRead(msgId);
        return ResponseEntity.ok(mensagemAtualizada);
    }

    @GetMapping("/nao-lidas")
    public ResponseEntity<List<ChatMessageResponse>> getMensagensNaoLidas(
            @RequestParam Long clienteId, @RequestParam Long prestadorId, @RequestParam boolean enviadoPorCliente) {
        List<ChatMessageResponse> naoLidas = chatService.getUnreadMessages(
            clienteId, prestadorId, enviadoPorCliente);
        return ResponseEntity.ok(naoLidas);
    }

    @PostMapping("/marcar-lidas")
    public ResponseEntity<Void> marcarVariasComoLidas(@RequestBody List<Long> messageIds) {
        chatService.markMessagesAsRead(messageIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ChatMessageResponse>> listarTodas() {
        List<ChatMessageResponse> todas = chatService.getAllMessages();
        return ResponseEntity.ok(todas);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<ChatMessageResponse>> listarMensagensCliente(@PathVariable Long clienteId) {
        List<ChatMessageResponse> msgs = chatService.getMensagensDoCliente(clienteId);
        return ResponseEntity.ok(msgs);
    }

    @GetMapping("/prestador/{prestadorId}")
    public ResponseEntity<List<ChatMessageResponse>> listarMensagensPrestador(@PathVariable Long prestadorId) {
        List<ChatMessageResponse> msgs = chatService.getMensagensDoPrestador(prestadorId);
        return ResponseEntity.ok(msgs);
    }


    @GetMapping("/conversas/cliente/{clienteId}")
    public ResponseEntity<List<ChatConversationResponse>> listarUltimaMensagemCliente(@PathVariable Long clienteId) {
        List<ChatConversationResponse> msgs = chatService.getConversasDoCliente(clienteId);
        return ResponseEntity.ok(msgs);
    }

    @GetMapping("/conversas/prestador/{prestadorId}")
    public ResponseEntity<List<ChatConversationResponse>> listarUltimaMensagemPrestador(@PathVariable Long prestadorId) {
        List<ChatConversationResponse> msgs = chatService.getConversasDoPrestador(prestadorId);
        return ResponseEntity.ok(msgs);
    }

}















