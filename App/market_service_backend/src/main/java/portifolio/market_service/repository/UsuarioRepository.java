package portifolio.market_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Usuario;


@RepositoryRestResource(collectionResourceRel = "usuarios", path = "usuarios")
public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    
}
