package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Avaliacao;

@RepositoryRestResource(collectionResourceRel="avaliacoes", path="avaliacoes")
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>{

    @Query("SELECT a FROM Avaliacao a JOIN FETCH a.cliente JOIN FETCH a.prestador")
    List<Avaliacao> findAllAvalicaoWithRelations();
}
