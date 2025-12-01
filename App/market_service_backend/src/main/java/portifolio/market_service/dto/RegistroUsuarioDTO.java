package portifolio.market_service.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioDTO(
    @NotBlank(message = "O nome é obrigatório")
    String nome,

    @NotBlank(message = "O CPF é obrigatório")
    @Digits(integer = 11, fraction = 0, message = "O CPF deve ter 11 dígitos. Somente números")
    String cpf,

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "Email inválido")
    String email,

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 4, max = 10, message = "Entre 4 e 10 caracteres")
    String senha,

    @Digits(integer = 8, fraction = 0, message = "O CEP deve ter 8 dígitos. Somente números")
    String cep,

    @NotBlank(message = "O tipo de usuário é obrigatório")
    String tipoUsuario,

    String telefone,
    String especializacao

    ) {}
