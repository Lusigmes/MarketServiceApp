package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import portifolio.market_service.model.entity.Prestador;

@RepositoryRestResource(collectionResourceRel = "prestadores", path = "prestadores")
public interface PrestadorRepository extends JpaRepository<Prestador, Long>{
   
    @Query("SELECT p FROM Prestador p JOIN FETCH p.usuario u LEFT JOIN FETCH u.prestador LEFT JOIN FETCH u.cliente ")
    List<Prestador> findAllWithUsuarioAndRelationPrestadors();

    @Query("SELECT p.id FROM Prestador p WHERE p.usuario.id = :id")
    long findPrestadorIdByUsuarioId(@Param("id") long usuarioId);
}
