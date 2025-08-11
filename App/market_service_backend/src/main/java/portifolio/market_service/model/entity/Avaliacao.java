package portifolio.market_service.model.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Campos: ID, nota (1-5), comentário, cliente (FK), prestador (FK).
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="avaliacao")
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @NotNull(message = "A Nota é obrigatória")
    @Column(nullable=false)
    private Integer nota;

    @NotNull(message = "O Comentário é obrigatório")
    @Column(nullable=false,  columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;

    @CreationTimestamp
    @JsonFormat(pattern="dd/MM/yyyy HH:mm:ss")
    @Column(name = "data_avaliacao", updatable = false)
    private LocalDateTime dataAvaliacao;

}
