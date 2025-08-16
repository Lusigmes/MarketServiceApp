package portifolio.market_service.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.StatusProposta;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="proposta")
public class Proposta {
    // Campos: ID, preço, comentário, status (enum), demanda (FK), prestador (FK).
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;
   
    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable=false)
    private BigDecimal preco;
    
    @Column(nullable=false,  columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "status_proposta", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusProposta statusProposta = StatusProposta.PENDENTE;

    @JsonBackReference
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "demanda_id", nullable = false)
    private Demanda demanda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;

    @CreationTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "data_criacao_proposta", nullable = false, updatable = false)
    private LocalDateTime dataCriacaoProposta;
    
    @UpdateTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "ultima_atualizacao", nullable = false)
    private LocalDateTime ultimaAtualizacao;


    @PrePersist
    public void prePersist(){
        this.dataCriacaoProposta = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public Long getPrestadorId(){
        return this.getPrestador().getId();
    }
    public Long getDemandaId(){
        return this.getDemanda().getId();
    }
}

