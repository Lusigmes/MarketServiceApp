package portifolio.market_service.dto;

import lombok.Data;

@Data
public class LoginTokenResponseDTO {
    private String token;
    private long expiresIn;


    public LoginTokenResponseDTO setToken(String token) {
        this.token = token;
        return this;
    }
    
    public LoginTokenResponseDTO setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
