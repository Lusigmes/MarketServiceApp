package portifolio.market_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.ChatMessageDTO;
import portifolio.market_service.dto.ChatMessageResponse;
import portifolio.market_service.model.entity.ChatMessage;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.enums.TipoChat;
import portifolio.market_service.repository.ChatRepository;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.repository.PrestadorRepository;

@Service
public class ChatService {
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private ClienteRepository clienteRepository;
    
    @Autowired
    private PrestadorRepository prestadorRepository;
    
    @Autowired
    private DemandaRepository demandaRepository;
    

    @Transactional
    public ChatMessageResponse save(ChatMessageDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.clienteId())
            .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Prestador prestador = prestadorRepository.findById(dto.prestadorId())
            .orElseThrow(() -> new EntityNotFoundException("Prestador não encontrado"));

        Demanda demanda = demandaRepository.findById(dto.demandaId())
            .orElseThrow(() -> new EntityNotFoundException("Demanda não encontrada"));

        ChatMessage msg = new ChatMessage();
        msg.setTipo(TipoChat.CHAT);
        msg.setCliente(cliente);
        msg.setPrestador(prestador);
        msg.setDemanda(demanda);
        msg.setConteudo(dto.conteudo().trim());
        msg.setEnviadoPorCliente(dto.enviadoPorCliente());
        msg.setLida(false);

        ChatMessage saved = chatRepository.save(msg);

        return chatToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getChatHistory(Long clienteId, Long prestadorId, Long demandaId){
        List<ChatMessage> msgs = chatRepository.findChatHistory(clienteId, prestadorId, demandaId);
        
        return msgs.stream()
            .map(this::chatToDTO)
            .collect(Collectors.toList());
    }
        
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getUnreadMessages(Long clienteId, Long prestadorId, Long demandaId, boolean enviadoPorCliente){
        boolean sentByCliente = enviadoPorCliente;
        List<ChatMessage> msgs =  chatRepository.findUnreadMessages(clienteId, prestadorId, demandaId, sentByCliente);
        return msgs.stream()
            .map(this::chatToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void markMessagesAsRead(List<Long> msgsIds){
        List<ChatMessage> msgs = chatRepository.findAllById(msgsIds);
        msgs.forEach(msg -> msg.setLida(true));
        chatRepository.saveAll(msgs);
    }

    @Transactional
    public ChatMessageResponse  markMessageAsRead(Long msgId){
        ChatMessage msg = chatRepository.findByIdWithDetails(msgId);
        if (msg == null) {
            throw new EntityNotFoundException("Mensagem não encontrada");
        }
        
        msg.setLida(true);
        ChatMessage saveMsg = chatRepository.save(msg);

        return chatToDTO(saveMsg);
    }

    @Transactional(readOnly = true)
    public ChatMessageResponse  getMessage(Long msgId){
        ChatMessage msg = chatRepository.findByIdWithDetails(msgId);
        if (msg == null) {
            throw new EntityNotFoundException("Mensagem não encontrada");
        }
        
        return chatToDTO(msg);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getAllMessages() {
        List<ChatMessage> msgs = chatRepository.findAllWithDetails();

        return msgs.stream()
            .map(this::chatToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public List<ChatMessageResponse> listToDTO( List<ChatMessage> msgs){
        return msgs.stream().map(this::chatToDTO).toList();
    }
     

    public ChatMessageResponse chatToDTO(ChatMessage entity) {
        return new ChatMessageResponse(
            entity.getId(),
            entity.getTipo(),
            entity.getCliente().getId(),
            entity.getNomeCliente(),
            entity.getPrestador().getId(),
            entity.getNomePrestador(),
            entity.getDemanda().getId(),
            entity.getNomeDemanda(),
            entity.getConteudo(),
            entity.isEnviadoPorCliente(),
            entity.isLida(),
            entity.getDataEnvio()
        );
    }

}
