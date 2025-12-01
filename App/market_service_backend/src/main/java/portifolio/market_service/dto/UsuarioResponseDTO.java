package portifolio.market_service.dto;

public record UsuarioResponseDTO(
    long id,
    String nome,
    String email,
    String cep,
    String tipoUsuario,
    String roleUsuario,
    String telefone,
    String especializacao
) { }
