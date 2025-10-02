package portifolio.market_service.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.model.enums.StatusProposta;

@RepositoryRestResource(collectionResourceRel = "propostas", path = "propostas")
public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    Page<Proposta> findByDemanda_Id(Long demandaId, Pageable pageable);

    Page<Proposta> findByPrestador_Id(Long prestadorId, Pageable pageable);

    List<Proposta> findByDemanda_Id(Long demandaId);

    boolean existsByDemanda_IdAndStatusProposta(long demandaId, StatusProposta status);

    @Query("SELECT p FROM Proposta p LEFT JOIN FETCH p.prestador WHERE p.id = :id")
    Proposta findPropostaById(@Param("id") long id);

    @Query("SELECT DISTINCT p.prestador FROM Proposta p WHERE p.demanda.id = :demandaId")
    List<Prestador> findPrestadoresByDemandaId(@Param("demandaId") long demandaId);

    @Query("SELECT DISTINCT p.prestador.id from Proposta p WHERE p.demanda.id = :demandaId")
    List<Long> findPrestadoresIdsByDemandaId(@Param("demandaId") long demandaId);
}
