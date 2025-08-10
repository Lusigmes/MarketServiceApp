package portifolio.market_service.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class LoginDTO {
    
    @Email(message = "Email inválido")
    private String email;
    private String senha;
}
