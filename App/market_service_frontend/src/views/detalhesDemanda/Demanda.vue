<script setup lang="ts">
import type { DemandaResponseInterface, PropostaResponseInterface } from '@/types';
import { computed, ref, watch } from 'vue';
import EditarDemandaForm from './EditarDemandaForm.vue';
import { corPrioridade, corStatus, labelPrioridade, labelStatus } from '@/utils/labelsUtils';
import PropostasRecebidas from './PropostasRecebidas.vue';
import { StatusDemanda, StatusProposta } from '@/types/enums';
import { atualizarDemanda } from '@/api/DemandaService';
import CriarPropostaForm from '../detalhesProposta/CriarPropostaForm.vue';
import { atualizarProposta, carregarTodasPropostasDaDemanda } from '@/api/PropostaService';

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


  const podeConcluirDemanda = computed(() => 
    permissaoClienteResponsavel.value && 
    props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO &&
    (props.demanda.propostaAceitaId === propostaAtualizadaRef.value?.id && 
      propostaAtualizadaRef.value?.statusProposta === StatusProposta.CONCLUIDA) 
    
  );

  const podeCancelarDemanda = computed(() => 
    permissaoClienteResponsavel.value && 
    (props.demanda.statusDemanda === StatusDemanda.ABERTA || 
    props.demanda.statusDemanda === StatusDemanda.EM_ANDAMENTO)
  );

  const podeDesfazerDemanda = computed(() => 
    permissaoClienteResponsavel.value && 
    (props.demanda.statusDemanda === StatusDemanda.CONCLUIDA || 
    props.demanda.statusDemanda === StatusDemanda.CANCELADA)
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
        alert("Já existe uma proposta aceita. Desfaça antes de aceitar outra.");
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
    } catch (err: any) {
      if (err.response?.data?.message?.includes("Já existe uma proposta ACEITA")) {
        alert("Já existe uma proposta aceita. Desfaça antes de aceitar outra.");
      } else {
        console.error(err);
        alert("Erro ao aceitar a proposta.");
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
    } catch (err) { console.error(err); }
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
        
      }else if( proposta.statusProposta === StatusProposta.RECUSADA){
        
        const desfazerPropostaRecusada = {
          statusProposta: StatusProposta.PENDENTE
        };
        const propostaAttUndo = await atualizarProposta(proposta.id, desfazerPropostaRecusada);

        propostaAtualizadaRef.value = propostaAttUndo;
        
      }
    } catch (err) {
        console.error(err);
    }
  };
  
  const concluirDemanda = async () => {
    try {
      const demandaAtualizada = {
        statusDemanda: StatusDemanda.CONCLUIDA,
        propostaAceitaId: null
      };
      await atualizarDemanda(props.demanda.id, demandaAtualizada, props.clienteId);
      
      emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
      
    } catch (err) { console.error(err); }
  };
  
  const cancelarDemanda = async () => {
    try {
      const demandaAtualizada = {
        statusDemanda: StatusDemanda.CANCELADA,
        propostaAceitaId: null
      };
      await atualizarDemanda(props.demanda.id, demandaAtualizada, props.clienteId);
      
      const propostasDaDemanda = await carregarTodasPropostasDaDemanda(props.demanda.id);

      for(const proposta of propostasDaDemanda){
        if(proposta.statusProposta !== StatusProposta.CONCLUIDA){
          await atualizarProposta(proposta.id, {statusProposta: StatusProposta.CANCELADA})
        }
      }
      
      emit("atualizar-demanda", {...props.demanda, ...demandaAtualizada});
      
      
    } catch (err) { console.error(err); }
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
            proposta.statusProposta === StatusProposta.CANCELADA
          ){
            await atualizarProposta(proposta.id, {statusProposta: StatusProposta.PENDENTE})
          }
        }

        emit("atualizar-demanda", {...props.demanda, ...demandaReaberta});


      }// 
       // else if (props.demanda.statusDemanda === StatusDemanda.CONCLUIDA) {
       // Voltar demanda para EM_ANDAMENTO

    } catch (err) {
      console.error("Erro ao desfazer ação da demanda:", err);
    }
  };
  
  const regarregarPropostas = ref(0);

  watch(() => props.demanda.statusDemanda, (novo, antigo) => {
    if(novo != antigo){
      regarregarPropostas.value++;
    }
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
        </div>

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
              @click="desfazerAcaoDemanda"
            >
            <v-icon left>mdi-undo</v-icon> 
            Reabrir Demanda
          </v-btn>
          
          <v-btn
            v-if="podeEnviarProposta"
              color="success"
              @click="abrirFormProposta"
            >
            <v-icon left>mdi-send</v-icon> 
            Enviar Proposta
          </v-btn>
          <v-btn
            v-if="podeConcluirDemanda"
              color="green"
              @click="concluirDemanda"
            >
            <v-icon left>mdi-check</v-icon>
            Concluir Demanda
          </v-btn>
          <v-spacer></v-spacer>
          <v-btn
            v-if="permissaoClienteResponsavel"
            color="primary"
            @click="abrirFormEdicao"
            >
            Editar
          </v-btn>
          <v-btn text color="red" @click="$emit('fechar')">
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
          @fechar="$emit('fechar')"
          @aceitar-proposta="atualizarDemandaAndPropostaAceita"
          @recusar-proposta="recusarProposta"
          @desfazer-proposta="desfazerPropostaAceitaOrRecusada"
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