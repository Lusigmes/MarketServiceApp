package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Cliente;

@RepositoryRestResource(collectionResourceRel = "clientes", path = "clientes")
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    
    @Query("SELECT c FROM Cliente c JOIN FETCH c.usuario u LEFT JOIN FETCH u.cliente LEFT JOIN FETCH u.prestador")
    List<Cliente> findAllWithUsuarioAndRelationsClientes();
}
