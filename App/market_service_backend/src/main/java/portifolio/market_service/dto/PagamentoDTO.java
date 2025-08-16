package portifolio.market_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import portifolio.market_service.model.enums.FormaPagamento;
import portifolio.market_service.model.enums.StatusPagamento;

public  record PagamentoDTO(

    @NotNull(message = "O Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    BigDecimal valor,
    
    @NotNull(message = "A Comissão é obrigatória")
    BigDecimal comissao,
    
    @NotNull(message = "O Status do Pagamento é obrigatório")
    StatusPagamento statusPagamento,

    @NotNull(message = "A Forma de Pagamento é obrigatória")
    FormaPagamento formaPagamento, 
    @NotNull(message = "A Proposta é obrigatória")
    long propostaId

) 
{}