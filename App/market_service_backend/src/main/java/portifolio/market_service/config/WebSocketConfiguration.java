package portifolio.market_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    
    @Autowired
    private final ChatWebSocketHandler chatWebSocketHandler;


    public WebSocketConfiguration(ChatWebSocketHandler chat){
        this.chatWebSocketHandler = chat;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
        .setAllowedOriginPatterns("*");
    }
}

