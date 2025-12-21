package portifolio.market_service.model.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="prestador")
public class Prestador implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @OneToOne(cascade = CascadeType.PERSIST, fetch=FetchType.LAZY) //(fetch=FetchType.LAZY)
    @JsonManagedReference(value = "prestador-usuario")
    @JoinColumn(name="usuario_id", nullable=false, unique=true)
    private Usuario usuario;

    @Column
    private String telefone;
    
    @Column
    private String especializacao;

    public String getNomeUsuarioPrestador(){
        return this.getUsuario().getNome();
    }
}