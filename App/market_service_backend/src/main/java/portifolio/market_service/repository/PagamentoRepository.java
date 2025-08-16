package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Pagamento;

@RepositoryRestResource(collectionResourceRel="pagamentos", path="pagamentos")
public interface PagamentoRepository extends JpaRepository<Pagamento, Long>{
 
    @Query("SELECT p FROM Pagamento p JOIN FETCH p.proposta pr JOIN FETCH pr.demanda d")
    
    List<Pagamento> findAllPropostaAndDemanda();


}
