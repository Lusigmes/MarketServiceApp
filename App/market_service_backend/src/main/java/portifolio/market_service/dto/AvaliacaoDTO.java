package portifolio.market_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoDTO(
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    Integer nota,

    @Size(max = 500, message = "O Comentário não pode exceder 500 caracteres")
    String comentario,

    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId,

    @NotNull(message = "O ID do prestador é obrigatório")
    Long prestadorId,

    Long demandaId

){ }
