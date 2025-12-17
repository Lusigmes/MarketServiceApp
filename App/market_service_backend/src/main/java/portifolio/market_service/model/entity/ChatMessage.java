package portifolio.market_service.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import portifolio.market_service.model.enums.TipoChat;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoChat tipo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demanda_id", nullable = false)
    private Demanda demanda;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String conteudo;
    
    @CreationTimestamp
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime dataEnvio;
    
    @Column(nullable = false)
    private boolean lida = false;
    
    @Column(nullable = false)
    private boolean enviadoPorCliente;
    
    @PrePersist
    public void prePersist() {
        this.dataEnvio = LocalDateTime.now();
        this.lida = false;
    }

    public String getNomeCliente(){
        return this.getCliente().getNomeUsuarioCliente();
    }
    public String getNomePrestador(){
        return this.getPrestador().getNomeUsuarioPrestador();
    }
    
    public String getNomeDemanda(){
        return this.getDemanda().getTitulo();
    }

}