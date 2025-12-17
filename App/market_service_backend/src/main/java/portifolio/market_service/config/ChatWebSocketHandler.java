package portifolio.market_service.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import portifolio.market_service.dto.ChatMessageDTO;
import portifolio.market_service.dto.ChatMessageResponse;
import portifolio.market_service.model.enums.TipoChat;
import portifolio.market_service.service.ChatService;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    
    // userId -> WebSocketSession
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    // sessionId -> userId
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ChatService chatService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Conexão WebSocket estabelecida: {}", session.getId());
        
        // extrair userId dos parâmetros da conexão
        String userId = extractUserIdFromSession(session);
        
        if (userId != null) {
            userSessions.put(userId, session);
            sessionToUser.put(session.getId(), userId);
            
            logger.info("Usuário {} conectado com sessão {}", userId, session.getId());
            
            sendWelcomeMessage(session, userId);
        } else {
            logger.warn("Conexão sem userId, fechando...");
            session.close();
        }
    }
    
    private String extractUserIdFromSession(WebSocketSession session) {
        // extrair userId dos parâmetros da conexão WebSocket
        String query = session.getUri().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("userId=")) {
                    return param.substring(7); // userId=123 -> 123
                }
            }
        }
        
        // Alternativa: tentar extrair do header
        Map<String, Object> attributes = session.getAttributes();
        if (attributes.containsKey("userId")) {
            return attributes.get("userId").toString();
        }
        
        return null;
    }

    private void sendWelcomeMessage(WebSocketSession session, String userId) throws Exception {
        ChatMessageResponse welcomeMessage = new ChatMessageResponse(
            null,                 
            TipoChat.SYSTEM,       
            null,           
            "Sistema",  
            null,        
            null,    
            null,       
            null,   
            "Conectado ao chat com sucesso!",
            false,               
            false,                 
            null              
        );

        session.sendMessage(
            new TextMessage(objectMapper.writeValueAsString(welcomeMessage))
        );
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Mensagem recebida de {}: {}", session.getId(), payload);
        
        try {
            ChatMessageDTO chatDTO = objectMapper.readValue(payload, ChatMessageDTO.class);

            String senderId = sessionToUser.get(session.getId());
            
            if (senderId == null) {
                logger.warn("Sessão não registrada: {}", session.getId());
                return;
            }
            
            processMessage(chatDTO, senderId);
            
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", e.getMessage());
            sendErrorMessage(session, "Erro ao processar mensagem: " + e.getMessage());
        }
    }
    
    private void processMessage(ChatMessageDTO messageDTO, String senderId) throws Exception {
        switch (messageDTO.tipo()) {
            case CHAT -> handleChatMessage(messageDTO, senderId);
            case JOIN -> handleJoinMessage(messageDTO, senderId);
            case READ -> handleReadReceipt(messageDTO);
            default -> logger.warn("Tipo de mensagem não suportado: {}", messageDTO.tipo());
        }
    }
    
    private void handleChatMessage(ChatMessageDTO chatDTO, String senderId) throws Exception {

        if (chatDTO.conteudo() == null || chatDTO.conteudo().trim().isEmpty()) {
            sendErrorMessage(senderId, "Mensagem não pode ser vazia");
            return;
        }

        ChatMessageResponse savedMessage = chatService.save(chatDTO);

        String destinatarioUserId = chatDTO.enviadoPorCliente()
            ? String.valueOf(chatDTO.prestadorId())
            : String.valueOf(chatDTO.clienteId());

        WebSocketSession recipientSession = userSessions.get(destinatarioUserId);
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(savedMessage))
            );
        }

        WebSocketSession senderSession = userSessions.get(senderId);
        if (senderSession != null && senderSession.isOpen()) {
            senderSession.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(savedMessage))
            );
        }
    }

    private void sendConfirmation(String userId, Long messageId) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {

            ChatMessageResponse confirmation = new ChatMessageResponse(
                null,
                TipoChat.SYSTEM,
                null,
                "Sistema",
                null,
                null,
                null,
                null,
                "Mensagem ID " + messageId + " enviada com sucesso",
                false,
                false,
                null
            );

            session.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(confirmation))
            );
        }
    }

    private void handleJoinMessage(ChatMessageDTO messageDTO, String userId) throws Exception {
        List<ChatMessageResponse> history =
            chatService.getChatHistory(
                messageDTO.clienteId(),
                messageDTO.prestadorId(),
                messageDTO.demandaId()
            );

        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            for (ChatMessageResponse msg : history) {
                session.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(msg))
                );
            }
        }
    }

    private void handleReadReceipt(ChatMessageDTO messageDTO) {
        if (messageDTO.messageId() != null) {
            chatService.markMessageAsRead(messageDTO.messageId());
        }
    }
    
    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws Exception {
        ChatMessageResponse errorDTO = new ChatMessageResponse(
            null,
            TipoChat.ERROR,
            null,
            "Sistema",
            null,
            null,
            null,
            null,
            errorMessage,
            false,
            false,
            null
        );

        session.sendMessage(
            new TextMessage(objectMapper.writeValueAsString(errorDTO))
        );
    }

    private void sendErrorMessage(String userId, String errorMessage) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            sendErrorMessage(session, errorMessage);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = sessionToUser.remove(session.getId());
        if (userId != null) {
            userSessions.remove(userId);
            logger.info("Usuário {} desconectado. Status: {}", userId, status);
        }
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Erro de transporte na sessão {}: {}", session.getId(), exception.getMessage());
    }
}