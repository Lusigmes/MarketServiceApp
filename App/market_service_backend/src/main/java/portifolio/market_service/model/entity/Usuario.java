package portifolio.market_service.model.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usuario")
public class Usuario implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @NotNull(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "O CPF é obrigatório")
    @Digits(integer = 11, fraction = 0, message = "O CPF deve ter 11 dígitos\nSomente números")
    @Column(unique=true, nullable = false)
    private long CPF;
    
    @Digits(integer = 8, fraction = 0, message = "O CEP deve ter 11 dígitos\\n" + //
        "Somente números")
    @Column
    private int CEP;

    @NotNull(message = "O Tipo de Usuário é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;


    // @ManyToOne(cascade = CascadeType.PERSIST)
    // @JoinColumn(name = "album_id", nullable = false)
    // @RestResource(exported = false)
}
