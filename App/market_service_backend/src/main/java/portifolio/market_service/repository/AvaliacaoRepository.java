package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Avaliacao;

@RepositoryRestResource(collectionResourceRel="avaliacoes", path="avaliacoes")
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long>{
    @Query("SELECT a FROM Avaliacao a " +
        "JOIN FETCH a.cliente " +
        "JOIN FETCH a.prestador")
        List<Avaliacao> findAllAvalicaoWithRelations();
        
        
    @Query("SELECT a FROM Avaliacao a " +
        "JOIN FETCH a.cliente c " +
        "JOIN FETCH c.usuario " +
        "JOIN FETCH a.prestador p " +
        "JOIN FETCH p.usuario " +
        "WHERE p.id = :prestadorId")
    Page<Avaliacao> findAvaliacaoByPrestadorId(@Param("prestadorId") Long prestadorId, Pageable pageable);
    
    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.prestador.id = :prestadorId")
    Double findMediaDasNotasDoPrestador(@Param("prestadorId") Long prestadorId);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.prestador.id = :prestadorId")
    Long findContadorDeAvaliacoesDoPrestador(@Param("prestadorId") Long prestadorId);

    
    @Query("SELECT a FROM Avaliacao a " +
        "JOIN FETCH a.cliente c " +
        "JOIN FETCH c.usuario " +
        "JOIN FETCH a.prestador p " +
        "JOIN FETCH p.usuario " +
        "WHERE c.id = :clienteId")
    Page<Avaliacao> findAvaliacaoByClienteId(@Param("clienteId") Long clienteId, Pageable pageable);

    @Query("SELECT a FROM Avaliacao a " +
        "JOIN FETCH a.cliente c " +
        "JOIN FETCH c.usuario " +
        "JOIN FETCH a.prestador p " +
        "JOIN FETCH p.usuario " +
        "WHERE a.id = :id")
    Avaliacao findAvaliacaoByIdWithRelations(@Param("id") Long id);

    @Query("SELECT COUNT(a) > 0 FROM Avaliacao a WHERE a.cliente.id = :clienteId AND a.prestador.id = :prestadorId AND a.demanda.id = :demandaId")
    boolean existeAvaliacaoEntreClientePrestadorDemanda(@Param("clienteId") Long clienteId, 
                                                   @Param("prestadorId") Long prestadorId,
                                                   @Param("demandaId") Long demandaId);
}
