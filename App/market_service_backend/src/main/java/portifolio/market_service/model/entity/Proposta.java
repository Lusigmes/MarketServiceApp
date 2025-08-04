package portifolio.market_service.model.entity;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "O Preço é obrigatório")
    @Column(nullable=false)
    private BigDecimal preco;
    
    @NotNull(message = "O Comentário é obrigatório")
    @Column(nullable=false,  columnDefinition = "TEXT")
    @Size(min = 10, max = 255, message = "O comentário deve ter entre 10 e 255 caracteres")
    private String comentario;

    @NotNull(message = "O Status da Proposta é obrigatório")
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
}
