package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import portifolio.market_service.model.entity.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long>{
    
    @Query("SELECT n FROM Notificacao n JOIN FETCH n.usuario WHERE n.usuario.id = :usuarioId ORDER BY n.dataCriacaoNotificacao DESC")
    List<Notificacao> findByUsuarioWithUsuarioOrderByDataCriacaoNotificacaoDesc(@Param("usuarioId") long usuarioId);

    @Query("SELECT COUNT(n) FROM Notificacao n WHERE n.usuario.id = :usuarioId AND n.lida = false")
    long countByUsuarioANdLidaFalse(@Param("usuarioId") long usuarioId);
}
