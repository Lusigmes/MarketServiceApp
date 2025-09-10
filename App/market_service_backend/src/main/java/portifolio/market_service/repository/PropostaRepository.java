package portifolio.market_service.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import portifolio.market_service.model.enums.StatusProposta;

import portifolio.market_service.model.entity.Proposta;

@RepositoryRestResource(collectionResourceRel = "propostas", path = "propostas")
public interface PropostaRepository extends JpaRepository<Proposta, Long> {

    Page<Proposta> findByDemanda_Id(Long demandaId, Pageable pageable);

    Page<Proposta> findByPrestador_Id(Long prestadorId, Pageable pageable);

    List<Proposta> findByDemanda_Id(Long demandaId);

    boolean existsByDemanda_IdAndStatusProposta(long demandaId, StatusProposta status);
}
