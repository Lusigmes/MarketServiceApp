package portifolio.market_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import portifolio.market_service.model.enums.StatusProposta;

public record PropostaDTO(

    @NotBlank(message = "O Título é obrigatório")
    @Size(min = 10, max = 100, message = "O título deve ter entre 10 e 100 caracteres")
    String titulo,
   
    @NotBlank(message = "O Comentário é obrigatório")
    @Size(min = 10, max = 255, message = "O comentário deve ter entre 10 e 255 caracteres")
    String comentario,
    
    @NotNull(message = "O Preço é obrigatório")
    BigDecimal preco, 
    
    @NotNull(message = "O Status da Proposta é obrigatório")
    StatusProposta statusProposta,
    
    @NotNull(message = "O ID da demanda é obrigatório")
    Long demandaId,
    
    @NotNull(message = "O ID do prestador éobrigatório")
    Long prestadorId
    




) 
{
    
}
