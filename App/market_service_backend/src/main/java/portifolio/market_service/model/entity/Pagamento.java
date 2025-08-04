package portifolio.market_service.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import portifolio.market_service.model.enums.StatusPagamento;

public class Pagamento {
    //Campos: ID, valor, comissão (10%), status, demanda (FK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    
    @NotNull(message = "O Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "comissao", nullable = false, precision = 10, scale = 2)
    private BigDecimal comissao = new BigDecimal("0.10");
    
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;  

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "demanda_id", nullable = false)
    private Demanda demanda;

    @Column(name = "data_pagamento")
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataPagamento;
}
