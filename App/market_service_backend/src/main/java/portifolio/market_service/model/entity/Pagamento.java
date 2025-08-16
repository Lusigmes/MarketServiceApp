package portifolio.market_service.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.FormaPagamento;
import portifolio.market_service.model.enums.StatusPagamento;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="pagamento")
public class Pagamento {
    //Campos: ID, valor, comissão (10%), status, demanda (FK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
    
   
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "comissao", nullable = false, precision = 10, scale = 2)
    private BigDecimal comissao = new BigDecimal("0.10");
    
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private StatusPagamento statusPagamento = StatusPagamento.PENDENTE;  
    
    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento = FormaPagamento.PIX;  
    
    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "demanda_id", nullable = false)
    private Demanda demanda;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "proposta_id", nullable = false)
    private Proposta proposta;
    
    @Column(name = "data_pagamento")
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataPagamento;

    @CreationTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "data_criacao_pagamento", nullable = false, updatable = false)
    private LocalDateTime dataCriacaoPagamento;
    
    @UpdateTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "ultima_atualizacao", nullable = false)
    private LocalDateTime ultimaAtualizacao;

    public Long getDemandaId(){
        return this.getDemanda().getId();
    }

    public Long getPropostaId(){
        return this.getProposta().getId();
    }
}
