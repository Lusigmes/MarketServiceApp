package portifolio.market_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Usuario;

@RepositoryRestResource(collectionResourceRel = "usuarios", path = "usuarios")
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    
    @Query("""
        SELECT u FROM Usuario u
        LEFT JOIN FETCH u.cliente
        LEFT JOIN FETCH u.prestador
        WHERE u.email = :email
    """)
    Optional<Usuario> findByEmailWithRelations(@Param("email") String email);   

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.cliente LEFT JOIN FETCH u.prestador")
    List<Usuario> findAllWithRelations();
}
