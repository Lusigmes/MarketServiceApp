package portifolio.market_service.dto;

public record PrestadorResponseDTO(
    long id,
    UsuarioResponseDTO usuario,
    String telefone,
    String especializacao
) {
    
}