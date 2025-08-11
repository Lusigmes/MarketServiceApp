package portifolio.market_service.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Proposta;

public record DemandaDTO(

    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres")
    @NotBlank(message="Categoria obrigatória")
    String descricao,
    
    @NotBlank(message="Categoria obrigatória")
    String categoria,
    
    @NotBlank(message="Localização obrigatória")
    String localizacao,
    
    @NotNull
    LocalDate prazo,
    
    @NotBlank(message="ID do cliente obrigatório")
    Cliente clienteId,
    
    @NotBlank(message="ID do cliente obrigatório")
    String statusDemanda,

    List<Proposta> propostas


) { }
/*
public record DemandaDTO(
    Long id, // pode ser null para novo registro

    @NotBlank(message = "A Descrição é obrigatória")
    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres")
    String descricao,

    @NotBlank(message = "A Categoria é obrigatória")
    String categoria,

    @NotBlank(message = "A Localização é obrigatória")
    String localizacao,

    @NotNull(message = "O Prazo é obrigatório")
    LocalDate prazo,

    String statusDemanda, // opcional, pode ter valor default no backend

    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId
) {}
{
  "descricao": "Preciso de um encanador para consertar vazamento.",
  "categoria": "Encanamento",
  "localizacao": "Rua das Flores, 123",
  "prazo": "2025-08-20",
  "statusDemanda": "ABERTA",
  "clienteId": 1
} */

/*
public record PagamentoDTO(
    Long id, // pode ser null para novo pagamento

    @NotNull(message = "O Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    BigDecimal valor,

    String statusPagamento, // opcional, pode ter valor default

    @NotNull(message = "O ID da demanda é obrigatório")
    Long demandaId
) {}
{
  "valor": 150.00,
  "statusPagamento": "PENDENTE",
  "demandaId": 5
} */