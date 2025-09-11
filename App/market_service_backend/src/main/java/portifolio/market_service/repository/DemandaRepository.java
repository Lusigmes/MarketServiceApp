package portifolio.market_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Demanda;

@RepositoryRestResource(collectionResourceRel = "demandas", path = "demandas")
public interface DemandaRepository extends JpaRepository<Demanda,Long> {
    @Query("SELECT d FROM Demanda d LEFT JOIN FETCH d.cliente c LEFT JOIN FETCH c.usuario WHERE d.id = :id")
    Demanda findDemandaByIdWithClienteAndUsuario(@Param("id") long id);
    
    @Query("SELECT d FROM Demanda d LEFT JOIN FETCH d.cliente WHERE d.id = :id")
    Demanda findDemandaById(@Param("id") long id);
}
    