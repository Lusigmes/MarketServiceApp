package portifolio.market_service.config;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import portifolio.market_service.service.JwtAuthService;

@Component
public class WebSocketHandShakeInterceptor implements HandshakeInterceptor {
    
 @Autowired
    private JwtAuthService jwtService;
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof org.springframework.http.server.ServletServerHttpRequest) {
            org.springframework.http.server.ServletServerHttpRequest servletRequest = 
                (org.springframework.http.server.ServletServerHttpRequest) request;
            HttpServletRequest httpServletRequest = servletRequest.getServletRequest();
            
            // 1. Tentar obter token da query string
            String queryString = httpServletRequest.getQueryString();
            String token = null;
            String userId = null;
            
            if (queryString != null) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    if (param.startsWith("token=")) {
                        token = param.substring(6);
                    } else if (param.startsWith("userId=")) {
                        userId = param.substring(7);
                    }
                }
            }
            
            // 2. Se não encontrar na query, tentar do header
            if (token == null) {
                token = httpServletRequest.getHeader("Authorization");
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
            }
            
            // 3. Validar token
            if (token != null && jwtService.isValidOnlyToken(token)) {
                String userEmail = jwtService.extractUsername(token);
                attributes.put("userEmail", userEmail);
                attributes.put("userId", userId);
                attributes.put("token", token);
                return true;
            }
            
            // 4. Token inválido ou ausente - recusar conexão
            System.out.println("WebSocket: Token inválido ou ausente. Query: " + queryString);
            return false;
        }
        
        return false;
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Nada a fazer após o handshake
    }
}