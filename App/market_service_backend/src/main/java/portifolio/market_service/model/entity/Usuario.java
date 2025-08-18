package portifolio.market_service.model.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
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
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    @NotNull(message = "O nome é obrigatório")
    private String nome;

    @Column(unique=true, nullable = false)
    @NotNull(message = "O CPF é obrigatório")
    private long CPF;
    
    @Column(unique=true, nullable = false)
    @NotNull(message = "O E-mail é obrigatório")
    private String email;
    
    @Column(unique=true, nullable = false)
    @NotNull(message = "A Senha é obrigatório")
    private String senha;
    
    @NotNull(message = "O CEP é obrigatório")
    @Column(nullable=false)
    private String CEP;

    @Column(name = "tipo_usuario", nullable = false)
    @NotNull(message = "O Tipo de Usuário é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUsuario;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role_usuario")
    private RoleUsuario roleUsuario; //= RoleUsuario.USER;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch=FetchType.LAZY)
    @JsonBackReference(value = "cliente-usuario")
    private Cliente cliente;
       
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch=FetchType.LAZY)
    @JsonBackReference(value = "prestador-usuario")
    private Prestador prestador;

    // AUTHS
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.roleUsuario.name())); 
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

}
