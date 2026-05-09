package portifolio.market_service.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityNotFoundException;
import portifolio.market_service.dto.DemandaDTO;
import portifolio.market_service.dto.DemandaResponseDTO;
import portifolio.market_service.dto.DemandaUpdateDTO;
import portifolio.market_service.model.entity.Cliente;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.entity.Prestador;
import portifolio.market_service.model.entity.Proposta;
import portifolio.market_service.model.entity.Usuario;
import portifolio.market_service.model.enums.PrioridadeDemanda;
import portifolio.market_service.model.enums.StatusDemanda;
import portifolio.market_service.model.enums.StatusProposta;
import portifolio.market_service.repository.ClienteRepository;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.repository.PrestadorRepository;
import portifolio.market_service.repository.PropostaRepository;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


//teste unitário
@ExtendWith(MockitoExtension.class)
class DemandaServiceTest{

    @Mock
    private DemandaRepository demandaRepository;
    
    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PropostaRepository propostaRepository;
    
    @Mock
    private PrestadorRepository prestadorRepository;
    
    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private DemandaService demandaService;

    private Cliente cliente;
    private Usuario usuarioCliente;
    private Usuario usuarioPrestador;
    private Demanda demanda;
    private DemandaDTO demandaDTO;
    private LocalDate prazo;
    private Proposta proposta;
    private Prestador prestador;

    @BeforeEach
    void setUp(){
        prazo = LocalDate.now().plusDays(30);

        usuarioCliente = new Usuario();
        usuarioCliente.setId(1L);
        usuarioCliente.setNome("Cliente Teste");
        usuarioCliente.setEmail("cliente@email.com");
        
        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setUsuario(usuarioCliente);
        
        usuarioPrestador = new Usuario();
        usuarioPrestador.setId(2L);
        usuarioPrestador.setNome("Prestador Teste");
        usuarioPrestador.setEmail("prestador@email.com");
        
        prestador = new Prestador();
        prestador.setId(2L);
        prestador.setUsuario(usuarioPrestador);

        demanda = new Demanda();
        demanda.setId(1L);
        demanda.setTitulo("Implementar interface"); 
        demanda.setDescricao("faça as telas usando vue3");
        demanda.setCategoria("Desenvolvimento");
        demanda.setLocalizacao("home office");
        demanda.setPrazo(prazo);
        demanda.setOrcamentoEstimado(new BigDecimal("500.00"));
        demanda.setPrioridade(PrioridadeDemanda.MEDIA);
        demanda.setStatusDemanda(StatusDemanda.ABERTA);
        demanda.setCliente(cliente);
        demanda.setDataCriacaoDemanda(LocalDateTime.now());
    
        proposta = new Proposta();
        proposta.setId(1L);
        proposta.setPreco(new BigDecimal("500.00"));
        proposta.setComentario("Proposta de serviço para interface");
        proposta.setStatusProposta(StatusProposta.PENDENTE);
        proposta.setDemanda(demanda);
        proposta.setPrestador(prestador);

        demandaDTO = new DemandaDTO(
            "Implementar interface",
            "faça as telas usando vue3",
            "Desenvolvimento", 
            "home office",
            prazo, 
            StatusDemanda.ABERTA, 
            new BigDecimal("500.00"), 
            PrioridadeDemanda.MEDIA, 
            1L, 
            null
        );
    }

        // cenários de teste
    /*
    * Teste: Salvar uma demanda com sucesso
    * 
    * Cenário: Cliente existe, dados válidos
    * Deve fazer: 
    * - .save() é chamado
    * - notificações enviadas
    * - demanda é retornada com dados corretos
    */
    @Test
    void deveSalvarDemandaComSucesso(){
        // preparo
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(demandaRepository.save(any(Demanda.class))).thenReturn(demanda);
        when(prestadorRepository.findAllWithUsuarioAndRelationPrestadors())
            .thenReturn(List.of());
        
        //ação
        Demanda resultado = demandaService.salvar(demandaDTO);
        
        //verificação
        assertNotNull(resultado, "Demanda salva não deve ser nulo");
        assertEquals("Implementar interface", resultado.getTitulo(), "O titulo dece ser igual ao informado");
        assertEquals(StatusDemanda.ABERTA, resultado.getStatusDemanda(),
            "Status base de uma demanda é ABERTA");
        
            //verificar mocks
        verify(clienteRepository).findById(1L);
        verify(demandaRepository).save(any(Demanda.class));
        verify(notificacaoService, times(1)).criarNotificacao(eq(usuarioCliente), anyString());    
        verify(prestadorRepository).findAllWithUsuarioAndRelationPrestadors();
    
    }
    /*
     * Teste: Atualizar apenas campos desejados
     * 
     * Cenário: apenas alguns campos preenchidos
     * Deve fazer: Atualizar apenas os campos informados
     */
    @Test
    void deveAtualizarDemandaApenasCamposInformados(){
        when(demandaRepository.findDemandaByIdWithClienteAndUsuario(1L)).thenReturn(demanda);
        when(demandaRepository.save(any(Demanda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
        
        String tituloOriginal = demanda.getTitulo();
        String descricaoOriginal = demanda.getDescricao();
        
        DemandaUpdateDTO dto = new DemandaUpdateDTO(
            null,
            null, "Atualizando Categoria",
            null,
            null, 
            null, 
            null, 
            null, 
            null);
    
        Demanda result = demandaService.atualizar(1L, dto, 1L);

        assertEquals(tituloOriginal, result.getTitulo());
        assertEquals(descricaoOriginal, result.getDescricao());
        assertEquals("Atualizando Categoria", result.getCategoria());
    }
    /*
    * Teste: Atualizar todos os campos
    * 
    * Cenário: modificar todos os atributos da demanda
    */
   @Test
   void deveAtualizarDemandaTodosCampos(){
        when(demandaRepository.findDemandaByIdWithClienteAndUsuario(1L)).thenReturn(demanda);
        when(demandaRepository.save(any(Demanda.class))).thenAnswer(invocacao -> invocacao.getArgument(0));
    
        LocalDate novoPrazo = LocalDate.now().plusDays(60);
        DemandaUpdateDTO dto = new DemandaUpdateDTO(
            "Alterar Layout",
            "Atualização geral", "TI",
            "Home Office",
            novoPrazo, 
            new BigDecimal("1000.50"), 
            PrioridadeDemanda.ALTA, 
            StatusDemanda.CANCELADA, 
            null);

        Demanda result = demandaService.atualizar(1L, dto, 1L);
        assertEquals("Alterar Layout", result.getTitulo());
        assertEquals("Atualização geral", result.getDescricao());
        assertEquals("TI", result.getCategoria());
        assertEquals("Home Office", result.getLocalizacao());
        assertEquals(novoPrazo, result.getPrazo());
        assertEquals(new BigDecimal("1000.50"), result.getOrcamentoEstimado());
        assertEquals(PrioridadeDemanda.ALTA, result.getPrioridade());
        assertEquals(StatusDemanda.CANCELADA, result.getStatusDemanda());

        verify(demandaRepository).save(any(Demanda.class));
    }
    
    //transição de status das demandas
    //validos
    @Test
    void deveAceitarTransicaoDeStatusValidoAbertaEmAndamento(){
        assertDoesNotThrow(() -> {
            Method method = DemandaService.class.getDeclaredMethod("regraStatusDemanda", StatusDemanda.class,StatusDemanda.class);
            method.setAccessible(true);
            method.invoke(demandaService, StatusDemanda.ABERTA, StatusDemanda.EM_ANDAMENTO);
        });
    }

    @Test
    void deveAceitarTransicaoDeStatusValidoAbertaCancelada(){
        assertDoesNotThrow(() -> {
            Method method = DemandaService.class.getDeclaredMethod("regraStatusDemanda", StatusDemanda.class,  StatusDemanda.class);
            method.setAccessible(true);
            method.invoke(demandaService, StatusDemanda.ABERTA, StatusDemanda.CANCELADA);
        });
    }

    @Test
    void deveAceitarTransicaoDeStatusValidoCanceladaAberta(){
        assertDoesNotThrow(() -> {
            Method method = DemandaService.class.getDeclaredMethod("regraStatusDemanda", StatusDemanda.class,  StatusDemanda.class);
            method.setAccessible(true);
            method.invoke(demandaService, StatusDemanda.CANCELADA, StatusDemanda.ABERTA);
        });
    }

    @Test
    void deveAceitarTransicaoDeStatusValidoEmAndamentoConcluida(){
        assertDoesNotThrow(() -> {
            Method method = DemandaService.class.getDeclaredMethod("regraStatusDemanda", StatusDemanda.class, StatusDemanda.class);
            method.setAccessible(true);
            method.invoke(demandaService, StatusDemanda.EM_ANDAMENTO, StatusDemanda.CONCLUIDA);
        });
    }
    //invalidos
    @Test
    void deveRejeitarTransicaoDeStatusInvalidoCanceladaEmAndamento(){
        Exception exception = assertThrows(Exception.class, () -> {
            Method method = DemandaService.class.getDeclaredMethod("regraStatusDemanda", StatusDemanda.class, StatusDemanda.class);
            method.setAccessible(true);
            method.invoke(demandaService, StatusDemanda.CANCELADA, StatusDemanda.EM_ANDAMENTO);
        });
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals(        
            "Status CANCELADA só pode ir para ABERTA",
            exception.getCause().getMessage()
        );
    }

    @Test
    void deveRejeitarTransicaoDeStatusInvalidoAbertoConcluida(){
        Exception exception = assertThrows(Exception.class, () -> {
            Method method = DemandaService.class.getDeclaredMethod("regraStatusDemanda", StatusDemanda.class, StatusDemanda.class);
            method.setAccessible(true);
            method.invoke(demandaService, StatusDemanda.ABERTA, StatusDemanda.CONCLUIDA);
        });
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals(        
            "Status ABERTA só pode ir para EM_ANDAMENTO ou CANCELADA",
            exception.getCause().getMessage()
        );
    }
    /*
    * Teste: Tentar salvar com cliente inexistente
    * 
    * Cenário: ID do cliente não existe no banco
    * Deve fazer: lançar RuntimeException
    * 
    */
    @Test
    void deveDarErroQuandoNaoExistirCliente(){
        //preparo
        when(clienteRepository.findById(999L)).thenReturn((Optional.empty()));
        //dto com cliente invalido
        DemandaDTO dtoInvalido = new DemandaDTO(
            "titulo", "descrição", "categoria", "local",
            prazo, StatusDemanda.ABERTA, BigDecimal.ZERO, 
            PrioridadeDemanda.BAIXA, 999L, null);

        //acao / verificar excessao
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> { demandaService.salvar(dtoInvalido); 
        });
        assertEquals("Cliente não encontrado", exception.getMessage());
        verify(clienteRepository).findById(999L);
        verify(demandaRepository, never()).save(any());
    }
    /*
    * Teste:  Buscar demanda por ID com sucesso
    */
   @Test
   void deveBuscarDemandaPorIdComSucesso(){
       when(demandaRepository.findDemandaById(1L)).thenReturn(demanda);
       Demanda result = demandaService.buscarDemandaPorId(1L);
       
       assertNotNull(result);
       assertEquals(1L, result.getId());
       verify(demandaRepository).findDemandaById(1L);
    }
    
    /*
    * Teste:  Buscar demanda por ID inexistente
    */
   @Test
   void deveFalharAoBuscarDemandaPorId(){
        when(demandaRepository.findDemandaById(999L)).thenReturn(null);
        
        //ação/verificação
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            ()-> { demandaService.buscarDemandaPorId(999L); }
        );
        
        assertTrue(exception.getMessage().contains("Demanda não encontrada"));
        verify(demandaRepository).findDemandaById(999L);
    }
    
    /*
    * Teste: Listar demandas paginadas
    */
    @Test
    void deveListarDemandasPaginadas(){
        //preparo
        Pageable pageable = PageRequest.of(0, 10);
        Page<Demanda> pageDemandas = new PageImpl<>(List.of(demanda), pageable, 1);
    
        when(demandaRepository.findAll(pageable)).thenReturn(pageDemandas);
        //açao
        Page<DemandaResponseDTO> result = demandaService.listarPaginado(pageable);
        //verificação
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Implementar interface", result.getContent().get(0).titulo());
        verify(demandaRepository).findAll(pageable);
    }

}