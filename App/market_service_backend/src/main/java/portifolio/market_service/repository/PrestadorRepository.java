package portifolio.market_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Prestador;

@RepositoryRestResource(collectionResourceRel = "prestadores", path = "prestadores")
public interface PrestadorRepository extends JpaRepository<Prestador, Long>{
    
}
