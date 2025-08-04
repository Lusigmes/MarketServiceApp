package portifolio.market_service.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.StatusDemanda;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="demanda")
public class Demanda implements Serializable{
    // Campos: ID, descrição, categoria, prazo, localização, status (enum), cliente (FK).

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @NotNull(message = "A Descrição é obrigatória")
    @Column(nullable=false,  columnDefinition = "TEXT")
    @Size(min = 10, max = 255, message = "A descrição deve ter entre 10 e 255 caracteres")
    private String descricao;

    @NotNull(message = "A Categoria é obrigatória")
    @Column(nullable=false)
    private String categoria;

    @NotNull(message = "A Localização é obrigatória")
    @Column(nullable=false)
    private String localizacao;

    @NotNull(message = "O Prazo é obrigatório")
    @Column(nullable=false)
    private LocalDate prazo;
    
    @NotNull(message = "O Status da Demanda é obrigatório")
    @Column(name = "status_demanda", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusDemanda statusDemanda = StatusDemanda.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy="demanda", cascade=CascadeType.ALL, orphanRemoval=true )
    @JsonManagedReference
    private List<Proposta> propostas = new ArrayList<>();
}

    // @NotNull(message = "O nome é obrigatório")
    // @Column(nullable = false)
//   @NotNull(message = "O Tipo de Usuário é obrigatório")
//     @Enumerated(EnumType.STRING)
//     @Column(name = "tipo_usuario", nullable = false)
    // @NotNull(message = "O CPF é obrigatório")
    // @Digits(integer = 11, fraction = 0, message = "O CPF deve ter 11 dígitos\nSomente números")
    // @Column(unique=true, nullable = false)