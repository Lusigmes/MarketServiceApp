package portifolio.market_service.dto;

public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email,
    String cep,
    String tipoUsuario,
    String roleUsuario
) { }
