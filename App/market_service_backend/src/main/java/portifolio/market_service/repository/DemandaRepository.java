package portifolio.market_service.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Usuario;

@RepositoryRestResource(collectionResourceRel = "demandas", path = "demandas")
public interface DemandaRepository extends JpaRepository<Demanda,Long> {
   @Query("SELECT d FROM Demanda d " +
       "LEFT JOIN FETCH d.cliente c " +
       "LEFT JOIN FETCH c.usuario " +
       "LEFT JOIN FETCH d.propostaAceita pa " +
       "LEFT JOIN FETCH pa.prestador p " +
       "LEFT JOIN FETCH p.usuario " +
       "WHERE d.id = :id")
    Demanda findDemandaByIdWithClienteAndUsuario(@Param("id") long id);
    
    @Query("SELECT d FROM Demanda d LEFT JOIN FETCH d.cliente WHERE d.id = :id")
    Demanda findDemandaById(@Param("id") long id);

     @Query("SELECT d FROM Demanda d WHERE d.cliente.usuario = :usuario ORDER BY d.dataCriacaoDemanda DESC")
    Page<Demanda> findDemandaByUsuario(@Param("usuario") Usuario usuario, Pageable pageable);

    @Query("SELECT COUNT(d) > 0 FROM Demanda d " +
       "WHERE d.cliente.id = :clienteId " +
       "AND d.propostaAceita IS NOT NULL " +
       "AND d.propostaAceita.prestador.id = :prestadorId " +
       "AND d.propostaAceita.statusProposta = 'ACEITA'")
    boolean existsByClienteIdAndPropostaAceitaPrestadorId(
        @Param("clienteId") Long clienteId, 
        @Param("prestadorId") Long prestadorId);
}
    