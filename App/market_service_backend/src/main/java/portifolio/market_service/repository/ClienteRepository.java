package portifolio.market_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Cliente;

@RepositoryRestResource(collectionResourceRel = "clientes", path = "clientes")
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    
}
