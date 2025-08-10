package portifolio.market_service.model.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.RoleUsuario;
import portifolio.market_service.model.enums.TipoUsuario;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="usuario")
public class Usuario implements UserDetails{
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
    
    @NotNull(message = "O E-mail é obrigatório")
    @Column(unique=true, nullable = false)
    private String email;
    
    @NotNull(message = "A Senha é obrigatório")
    @Size(min = 4, max = 10, message = "Entre 4 e 10 caracteres")
    @Column(unique=true, nullable = false)
    private String senha;

    @Digits(integer = 8, fraction = 0, message = "O CEP deve ter 11 dígitos\\n" + //
        "Somente números")
    @Column
    private int CEP;

    @NotNull(message = "O Tipo de Usuário é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_usuario", nullable = false)
    private TipoUsuario tipoUsuario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_usuario")
    private RoleUsuario roleUsuario = RoleUsuario.USER;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    // @JsonIgnore // Ignora a referência ao Cliente para evitar loop
    @JsonBackReference // Não será serializado
    private Cliente cliente;
       
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true)
    // @JsonIgnore // Ignora a referência ao Cliente para evitar loop
    @JsonBackReference // Não será serializado
    private Prestador prestador;


    // AUTHS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(); 
   }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // @ManyToOne(cascade = CascadeType.PERSIST)
    // @JoinColumn(name = "album_id", nullable = false)
    // @RestResource(exported = false)
}
