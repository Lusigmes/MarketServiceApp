package portifolio.market_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.DemandaDTO;
import portifolio.market_service.dto.DemandaResponseDTO;
import portifolio.market_service.dto.DemandaUpdateDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.model.enums.StatusDemanda;
import portifolio.market_service.model.enums.StatusProposta;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.repository.PrestadorRepository;
import portifolio.market_service.repository.PropostaRepository;

@Service
public class DemandaService {

    @Autowired
    private DemandaRepository demandaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private PrestadorRepository prestadorRepository;

    @Autowired
    private NotificacaoService notificacaoService;

    @Transactional
    public Demanda salvar(DemandaDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        Demanda demanda = new Demanda();

        demanda.setTitulo(dto.titulo());
        demanda.setDescricao(dto.descricao());
        demanda.setCategoria(dto.categoria());
        demanda.setLocalizacao(dto.localizacao());
        demanda.setPrazo(dto.prazo());
        demanda.setOrcamentoEstimado(dto.orcamentoEstimado());
        demanda.setPrioridade(dto.prioridade());
        demanda.setCliente(cliente);

        demanda.setStatusDemanda(dto.statusDemanda() != null ? dto.statusDemanda() : StatusDemanda.ABERTA);
       
        Demanda novaDemanda = demandaRepository.save(demanda);

        notificacaoService.criarNotificacao(
            cliente.getUsuario(),
            "Sua demanda \"" + demanda.getTitulo() + "\" foi criada com sucesso."
        );

        notificarTodosPrestadores(novaDemanda);

        return novaDemanda;
    }

    public List<DemandaResponseDTO> listar() {
        return demandaRepository.findAll()
                .stream().map(this::responseToDTO)
                .toList();
    }

    public Page<DemandaResponseDTO> listarPaginado(Pageable pageable) {
        return demandaRepository.findAll(pageable)
                .map(this::responseToDTO);
    }
    
    public Page<DemandaResponseDTO> listarDemandaByUsuario(Usuario usuario, Pageable pageable) {
        Page<Demanda> demandasPage = demandaRepository.findDemandaByUsuario(usuario, pageable);
        return demandasPage.map(this::responseToDTO);
    }

    public Demanda buscarDemandaPorId(Long id) {
        Demanda demanda = demandaRepository.findDemandaById(id);

        if (demanda == null) {
            throw new EntityNotFoundException("Demanda não encontrada com id: " + id);
        }
        return demanda;
    }
    public DemandaResponseDTO responseToDTO(Demanda demanda) {
        return new DemandaResponseDTO(
                demanda.getId(),
                demanda.getTitulo(),
                demanda.getDescricao(),
                demanda.getCategoria(),
                demanda.getLocalizacao(),
                demanda.getPrazo(),
                demanda.getOrcamentoEstimado(),
                demanda.getPrioridade(),
                demanda.getStatusDemanda(),
                demanda.getDataCriacaoDemanda(),
                demanda.getUltimaAtualizacao(),
                demanda.getClienteId(),
                demanda.getPropostaAceitaId()
        );
    }

    public Demanda atualizar(long id, DemandaUpdateDTO dto, long clienteId) {
        Demanda demanda = demandaRepository.findDemandaByIdWithClienteAndUsuario(id);

        if (demanda == null) {
            throw new EntityNotFoundException("Demanda não encontrada com id: " + id);
        }

        if (!demanda.getClienteId().equals(clienteId)) {
            throw new SecurityException("Você não tem permissão para atualizar esta demanda");
        }
        
        StatusDemanda statusAnterior = demanda.getStatusDemanda();
        Long propostaAceitaAnterior = demanda.getPropostaAceitaId();

        if (dto.titulo() != null) {
            demanda.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            demanda.setDescricao(dto.descricao());
        }
        if (dto.categoria() != null) {
            demanda.setCategoria(dto.categoria());
        }
        if (dto.localizacao() != null) {
            demanda.setLocalizacao(dto.localizacao());
        }
        if (dto.prazo() != null) {
            demanda.setPrazo(dto.prazo());
        }
        if (dto.orcamentoEstimado() != null) {
            demanda.setOrcamentoEstimado(dto.orcamentoEstimado());
        }

        if (dto.prioridade() != null) {
            demanda.setPrioridade(dto.prioridade());
        }

        //tratar alteração status
        if (dto.statusDemanda() != null && !dto.statusDemanda().equals(demanda.getStatusDemanda())) {
            regraStatusDemanda(demanda.getStatusDemanda(), dto.statusDemanda());
            demanda.setStatusDemanda(dto.statusDemanda());
             


            notificacoesStatusDemanda(demanda, statusAnterior, dto.statusDemanda());
        }
        
        //tratar proposta aceita
        if (dto.propostaAceitaId() != null) {
            Proposta propostaAceita = propostaRepository.findPropostaByIdWithRelations(dto.propostaAceitaId());
            
            if (propostaAceita == null) {
                    throw new EntityNotFoundException("Proposta não encontrada com id: " + dto.propostaAceitaId());
            }

            demanda.setPropostaAceita(propostaAceita);

            notificacaoService.notificarPropostaAceita(
                propostaAceita.getUsuarioPrestador(), demanda.getTitulo());

        // proposta aceita desfeita
        } else if(propostaAceitaAnterior != null && dto.propostaAceitaId() == null) {
            //evitar disparos de notificação quando status da demanda for alterado em outros cenários
            boolean ehReaberturaManual = dto.statusDemanda() == StatusDemanda.ABERTA && 
                                        (statusAnterior == StatusDemanda.EM_ANDAMENTO || 
                                        statusAnterior == StatusDemanda.ABERTA) && // estava com proposta aceita
                                        !foiCanceladaPeloPrestador(propostaAceitaAnterior); // verifica se não foi cancelamento da proposta
            
            if (ehReaberturaManual) {
                Proposta propostaCompleta = propostaRepository.findPropostaByIdWithRelations(propostaAceitaAnterior);
                
                if(propostaCompleta != null && propostaCompleta.getStatusProposta() != StatusProposta.CANCELADA) {
                    notificacaoService.criarNotificacao(
                        propostaCompleta.getUsuarioPrestador(), 
                        "A aceitação da sua proposta para a demanda \"" + demanda.getTitulo() + "\" foi cancelada. " +
                            "A demanda está novamente disponível"
                    );
                }
            }
            
            demanda.setPropostaAceita(null);
        }
        return demandaRepository.save(demanda);
    }

    private boolean foiCanceladaPeloPrestador(Long propostaId) {
        Proposta proposta = propostaRepository.findPropostaById(propostaId);
        return proposta != null && proposta.getStatusProposta() == StatusProposta.CANCELADA;
    }
    
    private void notificacoesStatusDemanda(Demanda demanda, StatusDemanda statusAnterior, StatusDemanda novoStatus){
        Usuario cliente = demanda.getClienteUsuario();
        String tituloDemanda = demanda.getTitulo();

        switch(novoStatus){
            case CANCELADA:
                notificacaoService.notificarDemandaCanceladaCliente(cliente, tituloDemanda);
            break;
            case CONCLUIDA:
                notificacaoService.notificarDemandaConcluidaCliente(cliente, tituloDemanda);
            break;
            case EM_ANDAMENTO:
                notificacaoService.criarNotificacao(cliente, "Sua demanda \"" + tituloDemanda + "\" está em andamento.");
            break;
            case ABERTA:
                notificacaoService.criarNotificacao(cliente, "Sua demanda \"" + tituloDemanda + "\" foi reaberta.");
            break;
        }

        if(novoStatus == StatusDemanda.CANCELADA || novoStatus == StatusDemanda.CONCLUIDA ||
            novoStatus == StatusDemanda.EM_ANDAMENTO || novoStatus == StatusDemanda.ABERTA){
            notificarPrestadoresVinculados(demanda, novoStatus);
        }
    }


    private void notificarPrestadoresVinculados(Demanda demanda, StatusDemanda novoStatus){
        String tituloDemanda = demanda.getTitulo();

        if(demanda.getPropostaAceita() != null){
            Usuario prestadorComPropostaAceita = demanda.getPrestadorResponsavelPorPropostaAceita();
            if(novoStatus == StatusDemanda.CONCLUIDA){
                notificacaoService.notificarDemandaConcluidaPrestador(
                    prestadorComPropostaAceita, tituloDemanda);
           
            }else if(novoStatus == StatusDemanda.CANCELADA){
                notificacaoService.notificarDemandaCanceladaPrestador(prestadorComPropostaAceita, tituloDemanda);
           
            }else if(novoStatus == StatusDemanda.EM_ANDAMENTO){
                notificacaoService.criarNotificacao(prestadorComPropostaAceita, 
                    "A demanda \"" + tituloDemanda + "\" que você está atendendo está em andamento.");
          
            } else if(novoStatus == StatusDemanda.ABERTA){
                notificacaoService.criarNotificacao(prestadorComPropostaAceita,
                    "A demanda \"" + tituloDemanda + "\" que você estava atendendo foi reaberta.");
            }
        }else {  
            List<Prestador> prestadoresVinculados = propostaRepository.findPrestadoresByDemandaIdWithUsuario(demanda.getId());
            if(prestadoresVinculados.isEmpty()){
                return;
            }
            
            String mensagemStatus = switch (novoStatus) {
                case CANCELADA -> "A demanda \"" + demanda.getTitulo() + "\" que você propôs foi cancelada.";
                case CONCLUIDA -> "A demanda \"" + demanda.getTitulo() + "\" que você propôs foi concluída.";
                case EM_ANDAMENTO -> "A demanda \"" + demanda.getTitulo() + "\" que você propôs está em andamento.";
                case ABERTA -> "A demanda \"" + demanda.getTitulo() + "\" que você propôs foi reaberta.";
                default -> null;
            };

            if(mensagemStatus != null){
                for(Prestador prestador: prestadoresVinculados){
                    notificacaoService.criarNotificacao(
                        prestador.getUsuario(), mensagemStatus);
                }
            }
        }

    }

    private void notificarTodosPrestadores(Demanda demanda){
        List<Prestador> todosPrestadores = prestadorRepository.findAllWithUsuarioAndRelationPrestadors();
        for(Prestador prestador: todosPrestadores){
            notificacaoService.notificarNovaDemanda(prestador.getUsuario(), demanda.getTitulo());
        }
    }

    private void regraStatusDemanda(StatusDemanda atual, StatusDemanda novo) {
        if (atual == null) {
            if (novo != StatusDemanda.ABERTA) {
                throw new IllegalArgumentException("Nova demanda deve iniciar com status ABERTA");
            }
            return;
        }

        switch (atual) {
            case ABERTA -> {
                if (!(novo == StatusDemanda.EM_ANDAMENTO || novo == StatusDemanda.CANCELADA)) {
                    throw new IllegalArgumentException("Status ABERTA só pode ir para EM_ANDAMENTO ou CANCELADA");
                }
            }
            case EM_ANDAMENTO -> {
                if (!(novo == StatusDemanda.CONCLUIDA || novo == StatusDemanda.CANCELADA || novo == StatusDemanda.ABERTA)) {
                    throw new IllegalArgumentException("Status EM_ANDAMENTO só pode ir para CONCLUIDA, CANCELADA ou ABERTA");
                }
            }
            case CANCELADA -> {
                if (!(novo == StatusDemanda.ABERTA)) {
                    throw new IllegalArgumentException("Status CANCELADA só pode ir para ABERTA");
                }
            }
            case CONCLUIDA -> {
                if (!(novo == StatusDemanda.ABERTA)) {
                    throw new IllegalArgumentException("Status CONCLUIDA só pode ir para ABERTA");
                }
            }
        }
    }
}
