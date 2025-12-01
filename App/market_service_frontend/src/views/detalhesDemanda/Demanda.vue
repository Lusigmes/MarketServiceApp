    <script setup lang="ts">
    import type { DemandaResponseInterface, PropostaResponseInterface } from '@/types';
    import { computed, ref, watch , onMounted} from 'vue';
    import EditarDemandaForm from './EditarDemandaForm.vue';
    import { corPrioridade, corStatus, labelPrioridade, labelStatus } from '@/utils/labelsUtils';
    import PropostasRecebidas from './PropostasRecebidas.vue';
    import { StatusDemanda, StatusProposta } from '@/types/enums';
    import { atualizarDemanda } from '@/api/DemandaService';
    import CriarPropostaForm from '../detalhesProposta/CriarPropostaForm.vue';
    import { atualizarProposta, carregarTodasPropostasDaDemanda, getPropostaById } from '@/api/PropostaService';
    import { useNotification } from "@/composables/useNotification";
    import GerarAvaliacaoForm from '../detalhesAvaliacoes/GerarAvaliacaoForm.vue';
    import { verificarAvaliacaoExistente } from '@/api/AvaliacaoService';

      const { showNotification } = useNotification();
      
      interface Props {
        demanda: DemandaResponseInterface;
        tipoUsuario: 'CLIENTE' | 'PRESTADOR';
        usuarioId: number;
        clienteId: number | null;
      }
        
      const props = defineProps<Props>();

      const emit = defineEmits<{
        (e: 'atualizar-demanda', payload: Partial<DemandaResponseInterface>): void;
        (e: 'fechar'): void;
      }>();

      const abaSelecionada = ref("detalhes"); 

      const permissaoClienteResponsavel = computed(() => 
        props.tipoUsuario === 'CLIENTE' && props.clienteId === props.demanda.clienteId
      );

      const podeEnviarProposta = computed(() => 
        props.tipoUsuario === 'PRESTADOR' && props.demanda.statusDemanda === StatusDemanda.ABERTA
      );

      const podeCancelarDemanda = computed(() => 
        permissaoClienteResponsavel.value && 
        (props.demanda.statusDemanda === StatusDemanda.ABERTA || 
        props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO)
      );

      const podeDesfazerDemanda = computed(() => 
        permissaoClienteResponsavel.value && 
        props.demanda.statusDemanda === StatusDemanda.CANCELADA
      );

      const propostaAceitaEstaConcluida = computed(() =>
        propostaAceita.value?.statusProposta === StatusProposta.CONCLUIDA
      );

      const podeConcluirDemanda = computed(() => 
        permissaoClienteResponsavel.value && 
        props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO &&
        propostaAceitaEstaConcluida.value
      );

      const podeAvaliar = computed(() => 
        permissaoClienteResponsavel.value && 
        props.demanda.statusDemanda === StatusDemanda.CONCLUIDA &&
        propostaAceita.value &&
        !avaliacaoExistente.value
      );
      

      const editando = ref(false); 
      const abrirFormEdicao = () => { editando.value = true; };
      const fecharFormEdicao = () => { editando.value = false; };
      
      const salvarEdicao = (demandaAtualizada: Partial<DemandaResponseInterface>) => {
        emit('atualizar-demanda', demandaAtualizada);
        fecharFormEdicao();
      }

      const formProposta = ref(false);

      const abrirFormProposta = () => {
        formProposta.value = true;
      };
      
      const fecharFormProposta = () => {
        formProposta.value = false;
      };
      
      const propostaCriada = () => {
        fecharFormProposta();
        showNotification("Proposta criada", "success");
      }

      const formAvaliacao = ref(false);
      const avaliacaoExistente = ref(false);
      const carregandoAvaliacao = ref(false);

      const abrirFormAvaliacao = () =>{
          formAvaliacao.value = true;
      }

      const fecharFormAvaliacao = () =>{
        formAvaliacao.value = false;
      }

      const avaliacaoCriada = () => {
          fecharFormAvaliacao();
          showNotification('Avaliação enviada com sucesso!', 'success');
          
          avaliacaoExistente.value = true;
      }
      
      const verificarAvaliacao = async () => {
          if (!propostaAceita.value || !props.clienteId || !props.demanda.id) {
              return;
          }
          
          carregandoAvaliacao.value = true;
          try {       
              const response = await verificarAvaliacaoExistente(
                  props.clienteId, 
                  propostaAceita.value.prestadorId!,
                  props.demanda.id
              );
              
              avaliacaoExistente.value = response.existeAvaliacao;
              
              if(!response.existeAvaliacao){
                showNotification("Avaliação disponível para esta demanda","success");
              }else{
                showNotification("Avaliação já relizada","warning");
              }
            } catch (error) {
              showNotification("Erro ao verificar avaliação","error");
              avaliacaoExistente.value = false;
          } finally {
              carregandoAvaliacao.value = false;
          }
      };

      const propostaAtualizadaRef = ref<PropostaResponseInterface | null>(null);

      const atualizarDemandaAndPropostaAceita = async (proposta: PropostaResponseInterface) => {
        try {
          if (props.demanda.propostaAceitaId && props.demanda.propostaAceitaId !== proposta.id) {
            showNotification("Já existe uma proposta aceita. Desfaça antes de aceitar outra.","warning")
            return;
          }
          const demandaAtualizada = {
            statusDemanda: StatusDemanda.EM_ANDAMENTO,
            propostaAceitaId: proposta.id
          };
          await atualizarDemanda(props.demanda.id, demandaAtualizada, props.clienteId);
        
          const propostaAtualizada = {
            statusProposta: StatusProposta.ACEITA
          }
          const propostaAtt = await atualizarProposta(proposta.id, propostaAtualizada);
          
          propostaAtualizadaRef.value = propostaAtt;
          
          emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
          showNotification("Sucesso ao aceitar proposta.", "success");
        } catch (err: any) {
          if (err.response?.data?.message?.includes("Já existe uma proposta ACEITA")) {
            showNotification("Já existe uma proposta aceita. Desfaça antes de aceitar outra.", "warning");
          } else {
            showNotification("Erro ao aceitar a proposta.", "error");
          }
        }

      };
      
      const recusarProposta = async (proposta: PropostaResponseInterface) => {
        try {
          const propostaRecusada = {
            statusProposta: StatusProposta.RECUSADA
          };
          const propostaAtt = await atualizarProposta(proposta.id, propostaRecusada);
          propostaAtualizadaRef.value = propostaAtt;   
          showNotification("Sucesso ao recusar a proposta.", "success");
        } catch (err) { showNotification("Erro ao recusar a proposta.", "error"); }
      };

      const desfazerPropostaAceitaOrRecusada = async (proposta: PropostaResponseInterface) => {
        try {
          if(proposta.statusProposta === StatusProposta.ACEITA){
            const desfazerDemandaEmAndamento = {
              statusDemanda: StatusDemanda.ABERTA,
              propostaAceitaId: null
            };
            await atualizarDemanda(props.demanda.id, desfazerDemandaEmAndamento, props.clienteId);
            
            const desfazerPropostaAceita = {
              statusProposta: StatusProposta.PENDENTE
            };
            const propostaAttUndo = await atualizarProposta(proposta.id, desfazerPropostaAceita);
            
            propostaAtualizadaRef.value = propostaAttUndo;
            emit("atualizar-demanda", {...props.demanda, ...desfazerDemandaEmAndamento});
            
            showNotification("Ação desfeita", "success");
          }else if( proposta.statusProposta === StatusProposta.RECUSADA){
            
            const desfazerPropostaRecusada = {
              statusProposta: StatusProposta.PENDENTE
            };
            const propostaAttUndo = await atualizarProposta(proposta.id, desfazerPropostaRecusada);
            
            propostaAtualizadaRef.value = propostaAttUndo;
            
            showNotification("Ação desfeita", "success");
          }
        } catch (err) {
            showNotification("Erro ao desfazer ação", "error");
        }
      };
      
      const propostaAceita = ref<PropostaResponseInterface | null>(null);
      const carregandoPropostaAceita = ref(false);

      const carregarPropostaAceita  = async () => {
        if( !props.demanda.propostaAceitaId ){
          propostaAceita.value = null;
          propostaAceitaCancelada.value = false;
          alertaPropostaCancelada.value = false;
          return;
        }

        carregandoPropostaAceita.value = true;

        try {
          const response = await getPropostaById(props.demanda.propostaAceitaId!);
          propostaAceita.value = response.data;

          if (response.data.statusProposta === StatusProposta.CANCELADA && 
            props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO) {
            propostaAceitaCancelada.value = true;
            alertaPropostaCancelada.value = true;
          } else {
            propostaAceitaCancelada.value = false;
            alertaPropostaCancelada.value = false;
          }
        } catch (error) {
          propostaAceita.value = null;
          propostaAceitaCancelada.value = false;
          alertaPropostaCancelada.value = false;
          showNotification("Erro ao carregar proposta aceita", "error");
          throw error;
          
        } finally {
          carregandoPropostaAceita.value = false;
        }
      };
      
      const concluirDemanda = async () => {
        
        if (!propostaAceitaEstaConcluida.value) {
          showNotification("Não é possível concluir a demanda. A proposta aceita não está concluída.", "error");
          return;
        }
        try {
          
          const demandaAtualizada = {
            statusDemanda: StatusDemanda.CONCLUIDA,
            propostaAceitaId: props.demanda.propostaAceitaId
          };
          await atualizarDemanda(props.demanda.id, demandaAtualizada, props.clienteId);
          
          emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
        } catch (err) { 
          showNotification("Erro ao concluir demanda", "error");
          throw err;
        }
      };
      
      const cancelarDemanda = async () => { 
        if(props.demanda.statusDemanda === "EM_ANDAMENTO"){
          showNotification("Não é possível cancelar uma demanda em andamento", "error");
          
          return;
        }
        try {
          const demandaAtualizada = {
            statusDemanda: StatusDemanda.CANCELADA,
            propostaAceitaId: null
          };
          await atualizarDemanda(props.demanda.id, demandaAtualizada, props.clienteId);
          
          const propostasDaDemanda = await carregarTodasPropostasDaDemanda(props.demanda.id);

          for(const proposta of propostasDaDemanda){
            if (proposta.statusProposta === StatusProposta.PENDENTE ||
              proposta.statusProposta === StatusProposta.ACEITA) {
              await atualizarProposta(proposta.id, { statusProposta: StatusProposta.RECUSADA }); 
            }else{
              showNotification("Não é possível cancelar uma demanda em andamento", "error");
            }
          }
          
          emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
          showNotification("Sucesso ao cancelar demanda", "success");

          
        } catch (err) { showNotification("Erro", "error");}
      };
      
      const desfazerAcaoDemanda = async () => {
        try {
          if(props.demanda.statusDemanda === StatusDemanda.CANCELADA){
            const demandaReaberta = {
              statusDemanda: StatusDemanda.ABERTA,
              propostaAceitaId: null
            };
            await atualizarDemanda(props.demanda.id, demandaReaberta, props.clienteId);

            const propostasDaDemanda = await carregarTodasPropostasDaDemanda(props.demanda.id);

            for(const proposta of propostasDaDemanda){
              if(proposta.statusProposta !== StatusProposta.CONCLUIDA &&
                proposta.statusProposta !== StatusProposta.CANCELADA 
              ){
                await atualizarProposta(proposta.id, {statusProposta: StatusProposta.PENDENTE})
              }
            }

            emit("atualizar-demanda", {...props.demanda, ...demandaReaberta});
            showNotification("Sucesso ao desfazer ação da demanda", "success");
          }
        } catch (err) {
          showNotification("Erro ao desfazer ação da demanda", "error");

        }
      };

      const reabrirDemandaAposCancelamento = async () => {
        try{
          const demandaReaberta = {
            statusDemanda: StatusDemanda.ABERTA,
            propostaAceitaId: null
          };
          await atualizarDemanda(props.demanda.id, demandaReaberta, props.clienteId);

          emit("atualizar-demanda", {...props.demanda, ...demandaReaberta});
          propostaAceitaCancelada.value = false;
          alertaPropostaCancelada.value = false;

          showNotification("Demanda reaberta com sucesso! Agora pode receber novas propostas.", "success");

        } catch (error) {
          showNotification("Erro ao reabrir demanda", "error");
        }
      };


      const propostaAceitaCancelada = ref(false);
      const alertaPropostaCancelada = ref(false);

      watch(() => propostaAceita.value?.statusProposta, 
        (novoStatus, statusAnterior) => {
          if(novoStatus === StatusProposta.CANCELADA &&
            props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO
          ){
            propostaAceitaCancelada.value = true;
            alertaPropostaCancelada.value = true;
            showNotification("A proposta aceita foi cancelada pelo prestador!", "warning");
          }else if(novoStatus !== StatusProposta.CANCELADA && propostaAceitaCancelada.value){
            propostaAceitaCancelada.value = false;
            alertaPropostaCancelada.value = false;
          }
        });
        
      watch(() => propostaAceita.value, 
        (novaProposta, propostaAnterior) => {
          if(novaProposta && 
          novaProposta.statusProposta === StatusProposta.CANCELADA &&
          props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO
        ){
            propostaAceitaCancelada.value = true;
            alertaPropostaCancelada.value = true;
            showNotification("A proposta aceita foi cancelada pelo prestador!", "warning");
          }
      },{deep:true});

      
      const regarregarPropostas = ref(0);

      watch(() => props.demanda.statusDemanda, (novo, antigo) => {
        if(novo != antigo){
          regarregarPropostas.value++;
        }
      });

      watch(() => props.demanda, async (novaDemanda) => {
        if(novaDemanda && novaDemanda.propostaAceitaId){
          await carregarPropostaAceita();
        }else{
          propostaAceita.value = null;
        }
      }, {immediate: true});

      watch(() => propostaAtualizadaRef.value, (novaProposta)=>{
        if(novaProposta && novaProposta.id === props.demanda.propostaAceitaId){
          propostaAceita.value = novaProposta;
        }
      });


      watch(() => propostaAceita.value, async (novaProposta) => {
        if (novaProposta && props.demanda.statusDemanda === StatusDemanda.CONCLUIDA) {
            await verificarAvaliacao();
        }
    });

      onMounted(() => {
        console.log("Demanda.vue montado");
        console.log("Demanda ID:", props.demanda.id);
        console.log("Status demanda:", props.demanda.statusDemanda);
      });
    </script>




    <template>
      <v-card
        :color="corStatus(props.demanda.statusDemanda)"
        class="pa-4 rounded-lg elevation-4 demanda-card"
      >
        <div class="dark-overlay"></div>
        
        <div class="tabs-container mb-4">
          <v-btn-toggle
            v-model="abaSelecionada"
            mandatory
            color="white"
            class="tabs-toggle"
            rounded="lg"
          >
            <v-btn
              value="detalhes"
              class="tab-btn"
              :class="{ 'tab-active': abaSelecionada === 'detalhes' }"
            >
              Detalhes
            </v-btn>
            
            <v-btn
              value="propostas"
              v-if="permissaoClienteResponsavel"
              class="tab-btn"
              :class="{ 'tab-active': abaSelecionada === 'propostas' }"
            >
              Propostas Recebidas
            </v-btn>
          </v-btn-toggle>
        </div>

        <v-window v-model="abaSelecionada" class="window-content">
          <v-window-item value="detalhes">
            <template v-if="!editando">
              <div class="d-flex justify-space-between align-start mb-4">
                <div class="flex-grow-1 mr-3">
                  <h3 class="text-h5 font-weight-bold text-white mb-1" style="text-shadow: 0 1px 2px rgba(0,0,0,0.3);">
                    {{ props.demanda.titulo }}
                  </h3>
                </div>
                
                <div class="priority-badge-large">
                  <v-icon :color="corPrioridade(props.demanda.prioridade)" size="22">
                    mdi-flag
                  </v-icon>
                  <span class="priority-text">
                    {{ labelPrioridade(props.demanda.prioridade) }}
                  </span>
                </div>
              </div>

              <div class="descricao-container mb-4">
                <div class="descricao-label d-flex align-center mb-1">
                  <v-icon color="white" size="16" class="mr-1">mdi-text</v-icon>
                  <span class="text-body-2 text-white font-weight-medium">Descrição</span>
                </div>
                <p class="descricao-text text-white mb-0">
                  {{ props.demanda.descricao }}
                </p>
              </div>
              
              <div class="chips-grid mb-4">
                <div class="chip-item">
                  <v-chip
                    size="small"
                    class="info-chip"
                    style="background: rgba(255, 255, 255, 0.15);"
                  >
                    <v-icon size="14" color="white" class="mr-1">mdi-tag</v-icon>
                    <span class="text-white font-weight-medium">
                      {{ props.demanda.categoria }}
                    </span>
                  </v-chip>
                </div>
                
                <div class="chip-item">
                  <v-chip
                    size="small"
                    class="info-chip"
                    style="background: rgba(255, 255, 255, 0.15);"
                  >
                    <v-icon size="14" color="white" class="mr-1">mdi-map-marker</v-icon>
                    <span class="text-white font-weight-medium">
                      {{ props.demanda.localizacao }}
                    </span>
                  </v-chip>
                </div>
                
                <div class="chip-item">
                  <v-chip
                    size="small"
                    class="info-chip"
                    style="background: rgba(255, 255, 255, 0.15);"
                  >
                    <v-icon size="14" color="white" class="mr-1">mdi-calendar-clock</v-icon>
                    <span class="text-white font-weight-medium">
                      {{ props.demanda.prazo }}
                    </span>
                  </v-chip>
                </div>
                
                <div class="chip-item">
                  <v-chip
                    size="small"
                    class="info-chip"
                    style="background: rgba(255, 255, 255, 0.15);"
                  >
                    <v-icon size="14" color="white" class="mr-1">mdi-cash</v-icon>
                    <span class="text-white font-weight-medium">
                      {{ props.demanda.orcamentoEstimado }}
                    </span>
                  </v-chip>
                </div>
              </div>

              <template v-if="props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO && propostaAceita">
                <div 
                  class="proposta-alert mb-4"
                  :style="{
                    background: propostaAceitaCancelada ? 
                      'rgba(255, 152, 0, 0.2)' : 
                      (propostaAceitaEstaConcluida ? 'rgba(76, 175, 80, 0.2)' : 'rgba(33, 150, 243, 0.2)'),
                    borderLeft: propostaAceitaCancelada ? 
                      '4px solid #FF9800' : 
                      (propostaAceitaEstaConcluida ? '4px solid #4CAF50' : '4px solid #2196F3')
                  }"
                >
                  <div class="d-flex align-center mb-2">
                    <v-icon 
                      :color="propostaAceitaCancelada ? 'orange' : (propostaAceitaEstaConcluida ? 'green' : 'blue')"
                      class="mr-2"
                    >
                      {{ propostaAceitaCancelada ? 'mdi-alert-circle' : 
                        (propostaAceitaEstaConcluida ? 'mdi-check-circle' : 'mdi-progress-clock') }}
                    </v-icon>
                    <span class="text-body-1 font-weight-bold text-white">
                      Proposta Aceita
                    </span>
                  </div>
                  
                  <div class="proposta-details">
                    <div class="d-flex justify-space-between mb-1">
                      <span class="text-caption text-white" style="opacity: 0.9;">Título:</span>
                      <span class="text-caption text-white font-weight-medium">{{ propostaAceita.titulo }}</span>
                    </div>
                    <div class="d-flex justify-space-between mb-1">
                      <span class="text-caption text-white" style="opacity: 0.9;">Status:</span>
                      <span class="text-caption text-white font-weight-medium">{{ propostaAceita.statusProposta }}</span>
                    </div>
                    <div class="d-flex justify-space-between">
                      <span class="text-caption text-white" style="opacity: 0.9;">Valor:</span>
                      <span class="text-caption text-white font-weight-medium">
                        R$ {{ propostaAceita.preco?.toFixed(2) }}
                      </span>
                    </div>
                    
                    <v-alert
                      v-if="alertaPropostaCancelada"
                      color="orange"
                      density="compact"
                      variant="tonal"
                      class="mt-2"
                    >
                      <span class="text-caption font-weight-bold">Atenção:</span> Esta proposta foi cancelada pelo prestador
                    </v-alert>
                  </div>
                </div>
              </template>

              <div class="actions-container">
                <div class="left-actions">
                  <v-btn
                    v-if="podeDesfazerDemanda"
                    color="warning"
                    rounded="lg"
                    @click="desfazerAcaoDemanda"
                    class="mr-2"
                    size="small"
                  >
                    Reabrir
                  </v-btn>
                  
                  <v-btn
                    v-if="propostaAceitaCancelada && props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO"
                    color="warning"
                    rounded="lg"
                    @click="reabrirDemandaAposCancelamento"
                    class="mr-2"
                    size="small"
                  >
                    Reabrir Demanda
                  </v-btn>
                  
                  <v-btn
                    v-if="podeEnviarProposta"
                    color="black"
                    rounded="lg"
                    @click="abrirFormProposta"
                    class="mr-2"
                    size="small"
                  >
                    Enviar Proposta
                  </v-btn>
                  
                  <v-btn
                    v-if="podeConcluirDemanda"
                    color="green"
                    rounded="lg"
                    @click="concluirDemanda"
                    class="mr-2"
                    size="small"
                  >
                    Concluir Demanda
                  </v-btn>
                  
                  <v-btn
                    v-if="podeAvaliar && !carregandoAvaliacao"
                    color="orange"
                    rounded="lg"
                    @click="abrirFormAvaliacao"
                    class="mr-2"
                    size="small"
                  >
                    Avaliar
                  </v-btn>
                  
                  <v-btn
                    v-else-if="carregandoAvaliacao"
                    color="orange"
                    rounded="lg"
                    disabled
                    class="mr-2"
                    size="small"
                  >
                    <v-progress-circular
                      indeterminate
                      size="16"
                      width="2"
                      class="mr-2"
                    />
                    Verificando...
                  </v-btn>
                </div>
                
                <div class="right-actions">
                  <v-btn
                      v-if="podeCancelarDemanda"
                      color="error"
                      rounded="lg"
                      @click="cancelarDemanda"
                      class="mr-2"
                      size="small"
                  >
                      Cancelar
                  </v-btn>
                  <v-btn
                    v-if="permissaoClienteResponsavel"
                    color="white"
                    rounded="lg"
                    @click="abrirFormEdicao"
                    class="mr-2"
                    size="small"
                  >
                    Editar
                  </v-btn>
                  
                  <v-btn 
                    text 
                    color="white" 
                    rounded="lg" 
                    @click="$emit('fechar')"
                    size="small"
                  >
                    Fechar
                  </v-btn>
                </div>
              </div>
            </template>

            <template v-else>
              <EditarDemandaForm
                :demanda="props.demanda"
                :cliente-id="props.clienteId"
                @cancelar="fecharFormEdicao"
                @salvar="salvarEdicao"
              />
            </template>
          </v-window-item>

          <v-window-item value="propostas">
            <PropostasRecebidas
              :demanda-id="props.demanda.id"
              :proposta-atualizada-prop="propostaAtualizadaRef"
              :recarregar="regarregarPropostas"
              :status-demanda="props.demanda.statusDemanda"
              @fechar="$emit('fechar')"
              @aceitar-proposta="atualizarDemandaAndPropostaAceita"
              @recusar-proposta="recusarProposta"
              @desfazer-proposta="desfazerPropostaAceitaOrRecusada"
            />
          </v-window-item>
        </v-window>

        <v-dialog v-model="formProposta" max-width="600px">
          <CriarPropostaForm
            :usuario-id="props.usuarioId"
            :demanda-id="props.demanda.id"
            @cancelar="fecharFormProposta"
            @criar-proposta="propostaCriada"
          />
        </v-dialog>

        <v-dialog v-model="formAvaliacao" max-width="500" persistent>
          <GerarAvaliacaoForm
            v-if="propostaAceita && props.clienteId"
            :prestador-id="propostaAceita.prestadorId!"
            :cliente-id="props.clienteId"
            :demanda-id="props.demanda.id"
            @cancelar="fecharFormAvaliacao"
            @avaliacao-criada="avaliacaoCriada"
          />
        </v-dialog>
      </v-card>
    </template>

    <style scoped>
    .demanda-card {
      position: relative;
      overflow-y: auto;
      max-height: 80vh;
      min-height: 400px;
    }

    .dark-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(to bottom, rgba(0, 0, 0, 0.15), rgba(0, 0, 0, 0.3));
      pointer-events: none;
      z-index: 1;
    }

    .demanda-card > *:not(.dark-overlay) {
      position: relative;
      z-index: 2;
    }

    .tabs-container {
      background: rgba(255, 255, 255, 0.1);
      border-radius: 12px;
      padding: 4px;
    }

    .tabs-toggle {
      background: transparent !important;
      width: 100%;
    }

    .tab-btn {
      flex: 1;
      height: 40px !important;
      color: rgba(255, 255, 255, 0.8) !important;
      background: transparent !important;
      transition: all 0.3s ease;
    }

    .tab-active {
      background: rgba(255, 255, 255, 0.2) !important;
      color: white !important;
      font-weight: 600;
    }

    .window-content {
      min-height: 300px;
      max-height: calc(80vh - 180px);
      overflow-y: auto;
    }

    .priority-badge-large {
      display: flex;
      flex-direction: column;
      align-items: center;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 12px;
      padding: 8px 12px;
      min-width: 100px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .priority-text {
      font-size: 0.7rem;
      font-weight: 700;
      color: #1a237e;
      margin-top: 4px;
      text-align: center;
    }

    .descricao-container {
      background: rgba(255, 255, 255, 0.1);
      border-radius: 8px;
      padding: 12px;
    }

    .descricao-label {
      opacity: 0.9;
    }

    .descricao-text {
      line-height: 1.5;
      opacity: 0.95;
    }

    .chips-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 8px;
    }

    .chip-item {
      display: flex;
    }

    .info-chip {
      width: 100%;
      backdrop-filter: blur(4px);
      transition: all 0.2s ease;
    }

    .info-chip:hover {
      transform: translateY(-2px);
      background: rgba(255, 255, 255, 0.25) !important;
    }

    .proposta-alert {
      border-radius: 8px;
      padding: 12px;
      backdrop-filter: blur(4px);
    }

    .proposta-details {
      background: rgba(0, 0, 0, 0.1);
      border-radius: 6px;
      padding: 10px;
    }

    .actions-container {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 8px;
      padding-top: 16px;
      border-top: 1px solid rgba(255, 255, 255, 0.1);
    }

    .left-actions, .right-actions {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
    }

    .text-white {
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
    }

    @media (max-width: 768px) {
      .actions-container {
        flex-direction: column;
        align-items: stretch;
      }
      
      .left-actions, .right-actions {
        justify-content: center;
        width: 100%;
      }
      
      .chips-grid {
        grid-template-columns: 1fr;
      }
      
      .priority-badge-large {
        min-width: 80px;
        padding: 6px 8px;
      }
    }
    </style>