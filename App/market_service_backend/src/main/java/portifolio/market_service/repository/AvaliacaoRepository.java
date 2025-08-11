package portifolio.market_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.dto.AvaliacaoDTO;
import portifolio.market_service.model.entity.Avaliacao;

@RepositoryRestResource(collectionResourceRel="avaliacoes", path="avaliacoes")
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>{
}
