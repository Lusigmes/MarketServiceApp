package portifolio.market_service.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;

public class WebSocketHandShakeInterceptor implements HandshakeInterceptor {
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            
            // extrai o userId da query string
            String query = httpServletRequest.getQueryString();
            if (query != null) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("userId=")) {
                        String userId = param.substring(7);
                        attributes.put("userId", userId);
                        System.out.println("WebSocket handshake para userId: " + userId);
                        return true;
                    }
                }
            }
            
            // Alternativa do header
            String userIdHeader = httpServletRequest.getHeader("X-User-Id");
            if (userIdHeader != null) {
                attributes.put("userId", userIdHeader);
                return true;
            }
        }
        
        System.out.println("WebSocket handshake sem userId");
        return false; // Aceita conexões mesmo sem userId
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception == null) {
            System.out.println("WebSocket handshake successful");
        } else {
            System.out.println("WebSocket handshake failed: " + exception.getMessage());
        }
    }
}