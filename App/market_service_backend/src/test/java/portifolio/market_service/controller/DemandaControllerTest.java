package portifolio.market_service.controller;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import portifolio.market_service.config.JwtAuthFilter;
import portifolio.market_service.config.RestConfiguration;
import portifolio.market_service.config.TestSecurityConfiguration;
import portifolio.market_service.dto.DemandaDTO;
import portifolio.market_service.dto.DemandaResponseDTO;
import portifolio.market_service.dto.DemandaUpdateDTO;
import portifolio.market_service.model.entity.Demanda;
import portifolio.market_service.model.enums.PrioridadeDemanda;
import portifolio.market_service.model.enums.StatusDemanda;
import portifolio.market_service.repository.DemandaRepository;
import portifolio.market_service.service.DemandaService;

//teste de integração
@WebMvcTest(
    value=DemandaController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = {JwtAuthFilter.class, RestConfiguration.class}
        )
    }
)
@ActiveProfiles("test")
@Import(TestSecurityConfiguration .class)
public class DemandaControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private DemandaService demandaService;
    
    @MockitoBean
    private DemandaRepository demandaRepository;

    private DemandaResponseDTO responseDTO;
    private DemandaDTO dto;
    private DemandaUpdateDTO updateDTO;
    private DemandaResponseDTO responseDTO2;
    private DemandaDTO dto2;
    private DemandaUpdateDTO updateDTO2;
    private LocalDate prazo;


    @BeforeEach
    void setUp(){
        prazo = LocalDate.now().plusDays(30);

        responseDTO = new DemandaResponseDTO(
            1L,
            "Migrar banco de dados",
            "Descrição detalhada do serviço",
            "TI",
            "Home Office",
            prazo,
            new BigDecimal("500.00"),
            PrioridadeDemanda.MEDIA,
            StatusDemanda.ABERTA,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            null
        );
        responseDTO2 = new DemandaResponseDTO(
            2L,
            "Autenticar usuarios novos",
            "Descrição detalhada do serviço",
            "TI",
            "Home Office",
            prazo,
            new BigDecimal("500.00"),
            PrioridadeDemanda.MEDIA,
            StatusDemanda.ABERTA,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            null
        );

        dto = new DemandaDTO(
            "Migrar banco de dados",
            "Descrição detalhada do serviço",
            "TI",
            "Home Office",
            prazo,
            StatusDemanda.ABERTA,
            new BigDecimal("500.00"),
            PrioridadeDemanda.MEDIA,
            1L,
            null
        );
        dto2 = new DemandaDTO(
            "Autenticar usuarios novos",
            "Descrição detalhada do serviço",
            "TI",
            "Home Office",
            prazo,
            StatusDemanda.ABERTA,
            new BigDecimal("500.00"),
            PrioridadeDemanda.MEDIA,
            1L,
            null
        );
        
        updateDTO = new DemandaUpdateDTO(   
            "Migrar banco de dados nosql",
            null,
            null,
            null,
            null,
            null,
            null,
            StatusDemanda.EM_ANDAMENTO,
            null
        );
        updateDTO2 = new DemandaUpdateDTO(    
            "Autenticar todos usuarios",
            null,
            null,
            null,
            null,
            null,
            null,
            StatusDemanda.EM_ANDAMENTO,
            null
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveCriarDemandaComSUcesso() throws Exception{
        Demanda novaDemanda = new Demanda();
    
        when(demandaService.salvar(any(DemandaDTO.class))).thenReturn(novaDemanda);
        when(demandaService.responseToDTO(any())).thenReturn(responseDTO);
        
        mockmvc.perform(post("/demandas")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.titulo").value("Migrar banco de dados"))
            .andExpect(jsonPath("$.statusDemanda").value("ABERTA"));

    }
    /*
    * Test: GET /demandas - listar todas as demandas paginadas
    * 
    * Cenário: usuário autenticado(mockado) faz requisição GET
    * Deve Fazer: status 200 OK e retorno 
    */
    @Test
    @WithMockUser
    void deveListarDemandasPaginadas() throws Exception{
        List<DemandaResponseDTO> demandass = List.of(responseDTO, responseDTO2);
        Page<DemandaResponseDTO> page = new PageImpl<>(demandass);
        when(demandaService.listarPaginado(any(Pageable.class))).thenReturn(page);
        
        mockmvc.perform(get("/demandas")
            .param("page", "0")     
            .param("size", "10")    
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk()) // verifica 200 ok
            .andExpect(jsonPath("$.content[0].id").value(1))
            .andExpect(jsonPath("$.content[0].titulo").value("Migrar banco de dados"))
            .andExpect(jsonPath("$.content[0].statusDemanda").value("ABERTA"))
            .andExpect(jsonPath("$.content[1].id").value(2))
            .andExpect(jsonPath("$.content[1].titulo").value("Autenticar usuarios novos"))
            .andExpect(jsonPath("$.content[1].statusDemanda").value("ABERTA"));
    }
    
    @Test
    @WithMockUser
    void deveBuscarDemandaPorId() throws Exception {
        when(demandaService.buscarDemandaPorId(2L)).thenReturn(new Demanda());
        when(demandaService.responseToDTO(any())).thenReturn(responseDTO2);
        mockmvc.perform(get("/demandas/{id}", 2L))
            .andDo(print())
            .andExpect(status().isOk()) 
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.titulo").value("Autenticar usuarios novos"));
    }

}
