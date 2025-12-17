package portifolio.market_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfiguration(ChatWebSocketHandler chat){
        this.chatWebSocketHandler = chat;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
            .setAllowedOriginPatterns("*")
            .addInterceptors(new HttpSessionHandshakeInterceptor());
            // .withSockJS();
    }
}
