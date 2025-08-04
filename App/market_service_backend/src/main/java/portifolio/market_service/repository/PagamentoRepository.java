package portifolio.market_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Pagamento;

@RepositoryRestResource(collectionResourceRel="pagamentos", path="pagamentos")
public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{
    
}
