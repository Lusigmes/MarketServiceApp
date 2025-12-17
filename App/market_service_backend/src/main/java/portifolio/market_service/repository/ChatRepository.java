package portifolio.market_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import portifolio.market_service.model.entity.ChatMessage;

@Repository
@RepositoryRestResource(collectionResourceRel = "chats", path = "chats")
public interface ChatRepository extends JpaRepository<ChatMessage, Long> {
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +  
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +  
           "LEFT JOIN FETCH cm.demanda d " +
           "WHERE c.id = :clienteId " +
           "AND p.id = :prestadorId " +
           "AND d.id = :demandaId " +
           "ORDER BY cm.dataEnvio ASC")
    List<ChatMessage> findChatHistory(
        @Param("clienteId") Long clienteId,
        @Param("prestadorId") Long prestadorId,
        @Param("demandaId") Long demandaId
    );
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +
           "LEFT JOIN FETCH cm.demanda d " +
           "WHERE c.id = :clienteId " +
           "AND p.id = :prestadorId " +
           "AND d.id = :demandaId " +
           "AND cm.lida = false " +
           "AND cm.enviadoPorCliente = :sentByCliente " +
           "ORDER BY cm.dataEnvio ASC")
    List<ChatMessage> findUnreadMessages(
        @Param("clienteId") Long clienteId,
        @Param("prestadorId") Long prestadorId,
        @Param("demandaId") Long demandaId,
        @Param("sentByCliente") boolean sentByCliente
    );
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +
           "LEFT JOIN FETCH cm.demanda d " +
           "WHERE cm.id = :id")
    ChatMessage findByIdWithDetails(@Param("id") Long id);  
    
    @Query("SELECT COUNT(cm) > 0 FROM ChatMessage cm " +
           "WHERE cm.cliente.id = :clienteId " +
           "AND cm.prestador.id = :prestadorId " +
           "AND cm.demanda.id = :demandaId")
    boolean existsConversation(
        @Param("clienteId") Long clienteId,
        @Param("prestadorId") Long prestadorId,
        @Param("demandaId") Long demandaId
    );
    
    @Query("SELECT cm FROM ChatMessage cm " +
       "LEFT JOIN FETCH cm.cliente c " +
       "LEFT JOIN FETCH c.usuario cu " +
       "LEFT JOIN FETCH cm.prestador p " +
       "LEFT JOIN FETCH p.usuario pu " +
       "LEFT JOIN FETCH cm.demanda d")
    List<ChatMessage> findAllWithDetails();
}