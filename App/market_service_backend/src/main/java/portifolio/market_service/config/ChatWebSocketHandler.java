package portifolio.market_service.config;

import java.time.LocalDateTime;
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

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.ChatMessageDTO;
import portifolio.market_service.dto.ChatMessageResponse;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.model.enums.TipoChat;
import portifolio.market_service.repository.UsuarioRepository;
import portifolio.market_service.service.ChatService;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatWebSocketHandler.class);
    
    // userId -> WebSocketSession
    private final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("Conexão WebSocket estabelecida: {}", session.getId());
        
        String userId = extractUserIdFromSession(session);
        
        if (userId != null && !userId.trim().isEmpty()) {
            userSessions.put(userId, session);
            logger.info("Usuário {} conectado. Total de sessões: {}", userId, userSessions.size());
            
            sendWelcomeMessage(session, userId);
        } else {
            logger.warn("Conexão sem userId válido, mas aceitando para testes");
            sendWelcomeMessage(session, "guest");
        }
    }
    
    private String extractUserIdFromSession(WebSocketSession session) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return null;
        }
        
        String query = session.getUri().getQuery();
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("userId=")) {
                String userId = param.substring(7);
                try {
                    Long.parseLong(userId);
                    return userId;
                } catch (NumberFormatException e) {
                    logger.warn("userId não é um número válido: {}", userId);
                    return userId; 
                }
            }
        }
        return null;
    }
    
    private void sendWelcomeMessage(WebSocketSession session, String userId) throws Exception {
        try {
            ChatMessageResponse welcomeMessage = new ChatMessageResponse(
                null,                 
                TipoChat.SYSTEM,       
                null,           
                "Sistema",  
                null,        
                null,       
                "Conectado ao chat!",
                false,               
                false,                 
                LocalDateTime.now()              
            );
            
            session.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(welcomeMessage))
            );
            
            logger.info("Mensagem de boas-vindas enviada para usuário {}", userId);
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de boas-vindas: {}", e.getMessage());
        }
    }
    
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Mensagem recebida de {}: {}", session.getId(), payload);
        
        try {
            ChatMessageDTO chatDTO = objectMapper.readValue(payload, ChatMessageDTO.class);
            
            // Verificar tipo de mensagem
            if (chatDTO.tipo() == null) {
                sendErrorMessage(session, "Tipo de mensagem não especificado");
                return;
            }
            
            switch (chatDTO.tipo()) {
                case CHAT -> {
                    logger.info("Processando mensagem CHAT de {} para {}", 
                        chatDTO.enviadoPorCliente() ? "cliente" : "prestador",
                        chatDTO.enviadoPorCliente() ? chatDTO.prestadorId() : chatDTO.clienteId());
                    handleChatMessage(chatDTO, session);
                }
                case JOIN -> {
                    logger.info("JOIN - Cliente {} conversando com prestador {}", 
                        chatDTO.clienteId(), chatDTO.prestadorId());
                    handleJoinMessage(chatDTO, session);
                }
                default -> {
                    logger.warn("Tipo não suportado: {}", chatDTO.tipo());
                    sendErrorMessage(session, "Tipo de mensagem não suportado: " + chatDTO.tipo());
                }
            }
            
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", e.getMessage(), e);
            sendErrorMessage(session, "Erro ao processar: " + e.getMessage());
        }
    }
    
    private void handleChatMessage(ChatMessageDTO chatDTO, WebSocketSession session) throws Exception {
        // Validar dados básicos
        if (chatDTO.conteudo() == null || chatDTO.conteudo().trim().isEmpty()) {
            sendErrorMessage(session, "Mensagem não pode ser vazia");
            return;
        }
        
        if (chatDTO.clienteId() == null || chatDTO.prestadorId() == null) {
            sendErrorMessage(session, "IDs inválidos");
            return;
        }
        
        try {
            logger.info("Salvando mensagem no banco...");
            
            // Aqui você precisa de um usuário autenticado
            // Para WebSocket, vamos usar um usuário fictício ou buscar o primeiro disponível
            // EM PRODUÇÃO: Você precisa implementar autenticação adequada
            
            // TEMPORÁRIO: Usar um usuário padrão para testes
            Usuario usuarioTeste = usuarioRepository.findById(1L) // ID do admin ou primeiro usuário
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado para testes"));
            
            // Salvar mensagem via serviço
            ChatMessageResponse savedMessage = chatService.save(chatDTO, usuarioTeste);
            
            logger.info("Mensagem salva com ID: {}", savedMessage.id());
            
            // Enviar de volta para confirmar
            session.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(savedMessage))
            );
            
            // Enviar para o destinatário se estiver conectado
            String destinatarioId = chatDTO.enviadoPorCliente() 
                ? String.valueOf(chatDTO.prestadorId())
                : String.valueOf(chatDTO.clienteId());
            
            WebSocketSession destinatarioSession = userSessions.get(destinatarioId);
            if (destinatarioSession != null && destinatarioSession.isOpen()) {
                logger.info("Enviando mensagem para destinatário: {}", destinatarioId);
                destinatarioSession.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(savedMessage))
                );
            } else {
                logger.info("Destinatário {} não está conectado", destinatarioId);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao salvar/enviar mensagem: {}", e.getMessage(), e);
            sendErrorMessage(session, "Erro: " + e.getMessage());
        }
    }
    
    private void handleJoinMessage(ChatMessageDTO messageDTO, WebSocketSession session) throws Exception {
        try {
            logger.info("Buscando histórico para cliente {}, prestador {}", 
                messageDTO.clienteId(), messageDTO.prestadorId());
            
            List<ChatMessageResponse> history = chatService.getChatHistory(
                messageDTO.clienteId(),
                messageDTO.prestadorId()
            );
            
            logger.info("Histórico encontrado: {} mensagens", history.size());
            
            // Enviar confirmação
            ChatMessageResponse confirmation = new ChatMessageResponse(
                null,
                TipoChat.SYSTEM,
                messageDTO.clienteId(),
                "Cliente",
                messageDTO.prestadorId(),
                "Prestador",
                "Histórico carregado: " + history.size() + " mensagens",
                false,
                false,
                LocalDateTime.now()
            );
            
            session.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(confirmation))
            );
            
            // Enviar histórico
            for (ChatMessageResponse msg : history) {
                session.sendMessage(
                    new TextMessage(objectMapper.writeValueAsString(msg))
                );
            }
            
            logger.info("Histórico enviado com sucesso");
            
        } catch (Exception e) {
            logger.error("Erro ao carregar histórico: {}", e.getMessage(), e);
            sendErrorMessage(session, "Erro ao carregar histórico");
        }
    }
    
    private void sendErrorMessage(WebSocketSession session, String errorMessage) throws Exception {
        try {
            ChatMessageResponse errorDTO = new ChatMessageResponse(
                null,
                TipoChat.ERROR,
                null,
                "Sistema",
                null,
                null,
                "[ERROR]: " + errorMessage,
                false,
                false,
                LocalDateTime.now()
            );
            
            session.sendMessage(
                new TextMessage(objectMapper.writeValueAsString(errorDTO))
            );
        } catch (Exception e) {
            logger.error("Erro ao enviar mensagem de erro: {}", e.getMessage());
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        logger.info("Sessão {} desconectada. Código: {}, Razão: {}", 
            session.getId(), status.getCode(), status.getReason());
        
        // Remover sessão
        userSessions.entrySet().removeIf(entry -> 
            entry.getValue().getId().equals(session.getId())
        );
        
        logger.info("Total de sessões ativas: {}", userSessions.size());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        logger.error("Erro de transporte na sessão {}: {}", session.getId(), exception.getMessage(), exception);
    }
    
    // Método auxiliar para verificar conexões
    public int getActiveConnectionsCount() {
        return userSessions.size();
    }
}