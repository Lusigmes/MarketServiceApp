package portifolio.market_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Proposta;

@RepositoryRestResource(collectionResourceRel = "propostas", path = "propostas")
public interface PropostaRepository extends JpaRepository<Proposta,Long>{
    
}
