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
    console.log("Proposta criada");

  }

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
      propostaAceita.value = null; return;
    }

    carregandoPropostaAceita.value = true;

    try {
      const response = await getPropostaById(props.demanda.propostaAceitaId!);
      propostaAceita.value = response.data;
    } catch (error) {
      propostaAceita.value = null;
      showNotification("Erro ao carregar proposta aceita", "error");
      throw error;
      
    } finally {
      carregandoPropostaAceita.value = false;
    }
  };
  
  const concluirDemanda = async () => {//RESOLVER AQUI
    
    if (!propostaAceitaEstaConcluida.value) {
      showNotification("Não é possível concluir a demanda. A proposta aceita não está concluída.", "error");
      return;
    }
    try {
      
      const demandaAtualizada = {
        statusDemanda: StatusDemanda.CONCLUIDA
        // propostaAceitaId: null
      };
      await atualizarDemanda(props.demanda.id, demandaAtualizada, props.clienteId);
      
      emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
    } catch (err) { 
      showNotification("Erro ao concluir demanda", "error");
      throw err;
    }
  };
  
  const cancelarDemanda = async () => { //RESOLVER AQUI
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
        if (proposta.statusProposta === StatusProposta.PENDENTE || //RESOLVER AQUI
          proposta.statusProposta === StatusProposta.ACEITA) {
          await atualizarProposta(proposta.id, { statusProposta: StatusProposta.RECUSADA }); //RESOLVER AQUI
        }else{
          showNotification("Não é possível cancelar uma demanda em andamento", "error");
        }
      }
      
      emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
      showNotification("Sucesso ao cancelar demanda", "success");

      
    } catch (err) { showNotification("Erro", "error");}
  };
  
  const desfazerAcaoDemanda = async () => {//RESOLVER AQUI
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
            // proposta.statusProposta === StatusProposta.ACEITA || //RESOLVER AQUI
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
  //8
    const handlePropostaAceitaCancelada = () =>{
      console.log('Evento recebido em demanda');
      tratarDemandaEmAndamentoComPropostaCancelada();
    }
//8
  const tratarDemandaEmAndamentoComPropostaCancelada = async () => {
    console.log("Função tratarDemandaEmAndamentoComPropostaCancelada chamada");
    try {      
      if (props.demanda.statusDemanda !== StatusDemanda.EM_ANDAMENTO) {
        console.log("Demanda não está em andamento, não é necessário tratar");
        return;
      }
      const demandaCanceladaPorProposta = {
        statusDemanda: StatusDemanda.ABERTA,
        propostaAceitaId: null
      };
      await atualizarDemanda(props.demanda.id, demandaCanceladaPorProposta, props.clienteId);
      emit("atualizar-demanda", {...props.demanda, ...demandaCanceladaPorProposta});
      showNotification("Demanda atualizada com sucesso", "success");
    } catch (err) {
      showNotification("Erro ao atualizar demanda após cancelamento", "error");
    }
  }
  
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

  onMounted(() => {
    console.log("Demanda.vue montado");
    console.log("Demanda ID:", props.demanda.id);
    console.log("Status demanda:", props.demanda.statusDemanda);
});
</script>

<template>
  <v-card
    :color="corStatus(props.demanda.statusDemanda)"
    class="pa-4 rounded-lg elevation-4"
  >
    <v-tabs v-model="abaSelecionada" background-color="white" grow>
      <v-tab value="detalhes">Detalhes</v-tab>
      <v-tab value="propostas" v-if="permissaoClienteResponsavel">Propostas Recebidas</v-tab>
    </v-tabs>

    <v-window v-model="abaSelecionada" class="mt-3">     
      <v-window-item value="detalhes">
        <template v-if="!editando">
          <div class="d-flex justify-space-between align-center mt-2 mb-3">
            <h3 class="text-h6 text-truncate" style="max-width: 70%;">
            {{ props.demanda.titulo }}
          </h3>
          <v-chip
          :color="corPrioridade(props.demanda.prioridade)"
          class="white--text"
          small
          >
          Urgência:
          {{ labelPrioridade(props.demanda.prioridade) }}
          </v-chip>
        </div>

        <p class="mb-3" style="line-height: 1.4em;">
          {{ props.demanda.descricao }}
        </p>
        
        <div class="d-flex flex-wrap gap-2 mb-3">
          <v-chip small outlined>
            <strong>Status:</strong> {{ labelStatus(props.demanda.statusDemanda) }}
          </v-chip>
          <v-chip small outlined>
            <strong>Categoria:</strong> {{ props.demanda.categoria }}
          </v-chip>
          <v-chip small outlined>
            <strong>Localização:</strong> {{ props.demanda.localizacao }}
          </v-chip>
          <v-chip small outlined>
            <strong>Prazo Proposto:</strong> {{ props.demanda.prazo }}
          </v-chip>
          <v-chip small outlined>
            <strong>Orçamento Estimado:</strong> {{ props.demanda.orcamentoEstimado }}
          </v-chip>
        </div>

    <!-- MOSTRAR INFO DA PROPOSTA ACEITA -->
        <v-alert
          v-if="props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO && propostaAceita"
          :color="propostaAceitaEstaConcluida ? 'green' : 'blue'"
          variant="outlined"
          class="mb-3"
        >
          <strong>Proposta Aceita:</strong> {{ propostaAceita.titulo }}
          <br>
          <strong>Status:</strong> {{ propostaAceita.statusProposta }}
          <br>
          <strong>Valor:</strong> R$ {{ propostaAceita.preco?.toFixed(2) }}
          
          <v-icon v-if="propostaAceitaEstaConcluida" color="green" class="ml-2">
            mdi-check-circle
          </v-icon>
          <v-icon v-else color="blue" class="ml-2">
            mdi-progress-clock
          </v-icon>
        </v-alert>

        <v-card-actions>
           <v-btn
            v-if="podeCancelarDemanda"
            icon
            color="red"
            class="position-absolute mt-10"
            style="top: 8px; right: 8px;"
            @click="cancelarDemanda"
        >
            <v-icon>mdi-delete</v-icon>
        </v-btn>
        
        
          
          <v-btn
            v-if="podeDesfazerDemanda"
              color="warning"
              rounded
              @click="desfazerAcaoDemanda"
            >
            Reabrir
          </v-btn>
          
          <v-btn
            v-if="podeEnviarProposta"
              color="secundary"
              rounded
              @click="abrirFormProposta"
            >
            Enviar Proposta
          </v-btn>
          <v-btn
            v-if="podeConcluirDemanda"
              color="green"
              rounded
              @click="concluirDemanda"
            >
            Concluir Demanda
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            v-if="permissaoClienteResponsavel"
            color="primary"
            rounded
            @click="abrirFormEdicao"
            >
            Editar
          </v-btn>
          <v-btn text color="red" rounded @click="$emit('fechar')">
            Fechar
          </v-btn>
        </v-card-actions>
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
          :demanda-id="props. demanda.id"
          :proposta-atualizada-prop="propostaAtualizadaRef"
          :recarregar="regarregarPropostas"
          :status-demanda="props.demanda.statusDemanda"
          @fechar="$emit('fechar')"
          @aceitar-proposta="atualizarDemandaAndPropostaAceita"
          @recusar-proposta="recusarProposta"
          @desfazer-proposta="desfazerPropostaAceitaOrRecusada"
          @proposta-aceita-cancelada="handlePropostaAceitaCancelada"
        />
      </v-window-item>

    <v-dialog v-model="formProposta" max-width="600px">
      <CriarPropostaForm
        :usuario-id="props.usuarioId"
        :demanda-id="props.demanda.id"
        @cancelar="fecharFormProposta"
        @criar-proposta="propostaCriada"
      />
    </v-dialog>
   </v-window>
  </v-card>
</template>

<style scoped>
.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>