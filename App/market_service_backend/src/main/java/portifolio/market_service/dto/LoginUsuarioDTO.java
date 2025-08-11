package portifolio.market_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record LoginUsuarioDTO(
    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 4, max = 10, message = "Entre 4 e 10 caracteres")
    String senha
) {}