package portifolio.market_service.dto;

public record UsuarioUpdateDTO(
    String nome,
    String email,
    String cep,
    String telefone,
    String especializacao
) {
    
}
