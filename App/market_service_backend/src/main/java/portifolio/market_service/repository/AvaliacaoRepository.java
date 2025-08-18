package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Avaliacao;

@RepositoryRestResource(collectionResourceRel="avaliacoes", path="avaliacoes")
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>{
    @Query("SELECT a FROM Avaliacao a " +
        "JOIN FETCH a.cliente " +
        "JOIN FETCH a.prestador")
    List<Avaliacao> findAllAvalicaoWithRelations();

        // retorna apenas os campos da AvaliacaoResponseDTO
    // @Query("SELECT new portifolio.market_service.dto.AvaliacaoResponseDTO(" +
    //    "a.id, a.nota, a.comentario, a.cliente.id, a.prestador.id, a.dataAvaliacao) " +
    //    "FROM Avaliacao a")
    // List<AvaliacaoResponseDTO> findAllAvaliacaoDTOs();

}
