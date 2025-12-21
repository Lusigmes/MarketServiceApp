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
           "WHERE c.id = :clienteId " +
           "AND p.id = :prestadorId " +
           "ORDER BY cm.dataEnvio ASC")
    List<ChatMessage> findChatHistory(
        @Param("clienteId") Long clienteId,
        @Param("prestadorId") Long prestadorId
    );
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +
           "WHERE c.id = :clienteId " +
           "AND p.id = :prestadorId " +
           "AND cm.lida = false " +
           "AND cm.enviadoPorCliente = :sentByCliente " +
           "ORDER BY cm.dataEnvio ASC")
    List<ChatMessage> findUnreadMessages(
        @Param("clienteId") Long clienteId,
        @Param("prestadorId") Long prestadorId,
        @Param("sentByCliente") boolean sentByCliente
    );
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +
           "WHERE cm.id = :id")
    ChatMessage findByIdWithDetails(@Param("id") Long id);  
    
    @Query("SELECT COUNT(cm) > 0 FROM ChatMessage cm " +
           "WHERE cm.cliente.id = :clienteId " +
           "AND cm.prestador.id = :prestadorId")
    boolean existsConversation(
        @Param("clienteId") Long clienteId,
        @Param("prestadorId") Long prestadorId
    );
    
    @Query("SELECT cm FROM ChatMessage cm " +
       "LEFT JOIN FETCH cm.cliente c " +
       "LEFT JOIN FETCH c.usuario cu " +
       "LEFT JOIN FETCH cm.prestador p " +
       "LEFT JOIN FETCH p.usuario pu")
    List<ChatMessage> findAllWithDetails();

    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +
           "WHERE c.id = :clienteId " +
           "ORDER BY cm.dataEnvio DESC")
    List<ChatMessage> findByClienteId(@Param("clienteId") Long clienteId);
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "LEFT JOIN FETCH cm.cliente c " +
           "LEFT JOIN FETCH c.usuario cu " +
           "LEFT JOIN FETCH cm.prestador p " +
           "LEFT JOIN FETCH p.usuario pu " +
           "WHERE p.id = :prestadorId " +
           "ORDER BY cm.dataEnvio DESC")
    List<ChatMessage> findByPrestadorId(@Param("prestadorId") Long prestadorId);
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.id IN (" +
           "   SELECT MAX(cm2.id) FROM ChatMessage cm2 " +
           "   WHERE cm2.cliente.id = :clienteId " +
           "   GROUP BY cm2.prestador.id" +
           ") " +
           "ORDER BY cm.dataEnvio DESC")
    List<ChatMessage> findLastMessagesByCliente(@Param("clienteId") Long clienteId);
    
    @Query("SELECT cm FROM ChatMessage cm " +
           "WHERE cm.id IN (" +
           "   SELECT MAX(cm2.id) FROM ChatMessage cm2 " +
           "   WHERE cm2.prestador.id = :prestadorId " +
           "   GROUP BY cm2.cliente.id" +
           ") " +
           "ORDER BY cm.dataEnvio DESC")
    List<ChatMessage> findLastMessagesByPrestador(@Param("prestadorId") Long prestadorId);
}