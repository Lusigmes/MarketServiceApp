package portifolio.market_service.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.PrioridadeDemanda;
import portifolio.market_service.model.enums.StatusDemanda;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "demanda")
public class Demanda implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    private String localizacao;

    @Column
    private LocalDate prazo;

    @Column(name = "status_demanda", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusDemanda statusDemanda;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade_demanda")
    private PrioridadeDemanda prioridade;

    @CreationTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "data_criacao_demanda", nullable = false, updatable = false)
    private LocalDateTime dataCriacaoDemanda;

    @UpdateTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "ultima_atualizacao", nullable = false)
    private LocalDateTime ultimaAtualizacao;

    @Column(precision = 10, scale = 2)
    private BigDecimal orcamentoEstimado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "demanda", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Proposta> propostas = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "proposta_aceita_id")
    private Proposta propostaAceita;

    // @Column //nullable = false
    // private boolean reaberta = false;
    @PrePersist
    public void prePersist() {
        this.dataCriacaoDemanda = LocalDateTime.now();
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    public Long getClienteId() {
        return this.getCliente().getId();
    }

    public Long getClienteUsuarioId() {
        return this.getCliente().getUsuario().getId();
    }

    public Long getPropostaAceitaId() {
        if (this.propostaAceita == null) {
            return null;
        }
        return this.propostaAceita.getId();
    }

}
