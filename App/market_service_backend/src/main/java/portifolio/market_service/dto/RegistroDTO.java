package portifolio.market_service.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroDTO {
    private String nome;
    @Digits(integer = 11, fraction = 0, message = "O CPF deve ter 11 dígitos\nSomente números")
    private String cpf;
    @Email(message = "Email inválido")
    private String email;
    @Size(min = 4, max = 10, message = "Entre 4 e 10 caracteres")
    private String senha;
    @Digits(integer = 8, fraction = 0, message = "O CEP deve ter 11 dígitos\\n" + //
        "Somente números")
    private String cep;
    private String tipoUsuario;
}
