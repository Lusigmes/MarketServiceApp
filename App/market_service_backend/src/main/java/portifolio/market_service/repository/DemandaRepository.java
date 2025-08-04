package portifolio.market_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Demanda;

@RepositoryRestResource(collectionResourceRel = "demandas", path = "demandas")
public interface DemandaRepository extends JpaRepository<Demanda,Long> {
    
}
    