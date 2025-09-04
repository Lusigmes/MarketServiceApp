package portifolio.market_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import portifolio.market_service.model.enums.PrioridadeDemanda;
import portifolio.market_service.model.enums.StatusDemanda;

public record DemandaDTO(
  
    @Size(min = 10, max = 100, message = "O titulo deve ter entre 10 e 100 caracteres")
    String titulo,
    
    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres")
    @NotBlank(message="Categoria obrigatória")
    String descricao,
    
    @NotBlank(message="Categoria obrigatória")
    String categoria,
    
    @NotBlank(message="Localização obrigatória")
    String localizacao,
    
    @NotNull(message="Prazo obrigatório")
    LocalDate prazo,
    
    StatusDemanda statusDemanda,
    
    BigDecimal orcamentoEstimado,
    
    PrioridadeDemanda prioridade,
    
    @NotNull(message="ID do cliente obrigatório")
    long clienteId,

    long propostaAceitaId
    // List<Proposta> propostas


) { }
