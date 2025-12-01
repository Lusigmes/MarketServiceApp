<script setup lang="ts">
import type { PropostaResponseInterface } from '@/types';
import { computed, onMounted, ref, watch } from 'vue';
import Proposta from '../detalhesProposta/Proposta.vue';
import { corStatusProposta } from '@/utils/labelsUtils';
import { carregarPropostasDaDemanda } from '@/api/PropostaService';
import { usePropostaPagination } from '@/composables/usePagination';
import { StatusProposta, StatusDemanda } from '@/types/enums';

  interface Props {
    demandaId: number;
    propostaAtualizadaProp: PropostaResponseInterface | null ;
    recarregar?: number;
    statusDemanda?: StatusDemanda;
  }

  const props = defineProps<Props>();

  const emit = defineEmits<{
    (e: 'fechar'): void;
    (e: 'aceitar-proposta', payload: PropostaResponseInterface): void;
    (e: 'recusar-proposta', payload: PropostaResponseInterface): void;
    (e: 'desfazer-proposta', payload: PropostaResponseInterface): void;
  }>();

  const {
    items: propostas,
    page, totalPages,
    loading, atualizarPagina 
  } = usePropostaPagination<PropostaResponseInterface>(
    carregarPropostasDaDemanda, props.demandaId, 5);
  
  const propostaSelecionada = ref<PropostaResponseInterface | null>(null);
  const propostaAtual = computed(() => propostaSelecionada.value);

  const dialogDetalhe = ref(false);

  const abrirDetalheProposta = (proposta: PropostaResponseInterface) => {
      propostaSelecionada.value = proposta;
      dialogDetalhe.value = true;
    };
    
  const fecharDetalheProposta = () => {
    dialogDetalhe.value = false;
    propostaSelecionada.value = null;
  };

  const aceitarProposta = () => {
    if(!propostaAtual.value) return;
      emit("aceitar-proposta", propostaAtual.value);
      fecharDetalheProposta();
    }
  
  const recusarProposta = () => {
    if(!propostaAtual.value) return;
    emit("recusar-proposta", { ...propostaAtual.value } as PropostaResponseInterface);
    fecharDetalheProposta();
  }
  const desfazerProposta = () => {
    if(!propostaAtual.value) return;
    emit('desfazer-proposta', {...propostaAtual.value} as PropostaResponseInterface);
    fecharDetalheProposta();
  }

  const mudarPagina = async (novaPagina: number) => {
    await atualizarPagina(novaPagina-1);
  };
  
  const atualizarPropostaListagem = (propostaAtt: PropostaResponseInterface) => {
    const index = propostas.value.findIndex(p => p.id === propostaAtt.id);
    if(index !== -1) {
      propostas.value[index] = {...propostaAtt};
    }
    if(propostaSelecionada.value?.id === propostaAtt.id) {
      propostaSelecionada.value = {...propostaSelecionada.value, ...propostaAtt};
      dialogDetalhe.value = true;
    }
  };
  watch(
  () => props.propostaAtualizadaProp,
    (propostaListagemAtt) => {
      if (propostaListagemAtt) atualizarPropostaListagem(propostaListagemAtt);
    }
  );
  watch(
  () => props.recarregar,
    async (novo) => {
      if (novo && novo > 0){
        await atualizarPagina();
    }
  });

  onMounted(async () => {  
    await atualizarPagina();
  });
</script>



<template>
  <div>
    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" />
    </v-row>

    <template v-else>
      <!-- Listagem -->
      <v-list v-if="!propostaAtual" class="propostas-list">
        <div v-if="propostas.length === 0" class="text-center py-6">
          <v-icon size="48" color="rgba(0, 0, 0, 0.5)" class="mb-3">
            mdi-file-document-outline
          </v-icon>
          <p class="text-body-1 text-black">Nenhuma proposta recebida</p>
        </div>

        <v-list-item 
          v-for="prop in propostas" 
          :key="prop.id" 
          @click="abrirDetalheProposta(prop)"
          class="cursor-pointer rounded mb-2 proposta-item"
          :class="corStatusProposta(prop.statusProposta)"
        >
          <div class="dark-overlay"></div>
          
          <v-list-item-title class="text-white font-weight-bold">
            {{ prop.titulo }}
          </v-list-item-title>
          <v-list-item-subtitle class="text-white" style="opacity: 0.9;">
            Preço: R$ {{ prop.preco }} - {{ prop.dataCriacao }}
          </v-list-item-subtitle>
          
          <div class="status-chip">
            <v-chip
              size="x-small"
              style="background: rgba(255, 255, 255, 0.2);"
            >
              <span class="text-caption text-white font-weight-medium">
                {{ prop.statusProposta }}
              </span>
            </v-chip>
          </div>
        </v-list-item>
      </v-list>

      <v-row justify="center" class="mt-4" v-if="!propostaAtual && totalPages > 1">
        <v-pagination
          :length="totalPages"
          :model-value="page + 1"
          @update:model-value="mudarPagina"
          color="black"
          size="small"
          rounded
        />
      </v-row>

      <div v-else-if="propostaAtual" class="proposta-detalhes">
        <div class="d-flex align-center mb-4">
          <h4 class="text-h6 font-weight-bold text-white mb-0">Detalhes da Proposta</h4>
        </div>

        <Proposta 
          :proposta="propostaAtual" 
          :status-demanda="props.statusDemanda"
        />

        <div class="actions-container mt-4">
          <div class="left-actions">
            <template v-if="propostaAtual && propostaAtual.statusProposta === StatusProposta.PENDENTE">
              <v-btn 
                color="green"
                rounded="lg"
                @click="aceitarProposta"
                class="mr-2"
                size="small"
              >
                Aceitar
              </v-btn>
              <v-btn 
                color="red"
                rounded="lg"
                @click="recusarProposta"
                class="mr-2"
                size="small"
              >
                Recusar
              </v-btn>
            </template>

            <template v-if="propostaAtual &&
                            (propostaAtual.statusProposta === StatusProposta.ACEITA ||
                            propostaAtual.statusProposta === StatusProposta.RECUSADA) ">
              <v-btn 
                v-if="props.statusDemanda !== StatusDemanda.CANCELADA" 
                color="orange"
                rounded="lg"
                @click="desfazerProposta"
                class="mr-2"
                size="small"
              >
                Cancelar ação
              </v-btn>
            </template>
          </div>
          
          <div class="right-actions">
            <v-btn 
              color="white" 
              rounded="lg" 
              @click="fecharDetalheProposta"
              size="small"
            >
              Voltar
            </v-btn>
          </div>
        </div>
      </div>
    </template>

    <div class="actions-container mt-3">
      <v-spacer></v-spacer>
      <div class="right-actions">
        <v-btn 
          color="white" 
          rounded="lg" 
          @click="$emit('fechar')"
          size="small"
        >
          Fechar
        </v-btn>
      </div>
    </div>
  </div>
</template>

<style scoped>
.propostas-list {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 4px;
}

.propostas-list::-webkit-scrollbar {
  width: 6px;
}

.propostas-list::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.propostas-list::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.proposta-item {
  position: relative;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
  overflow: hidden;
}

.proposta-item:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  transform: translateY(-4px);
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

.proposta-item > *:not(.dark-overlay) {
  position: relative;
  z-index: 2;
}

.status-chip {
  position: absolute;
  top: 8px;
  right: 8px;
}

.text-white {
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.proposta-detalhes {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
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

@media (max-width: 768px) {
  .propostas-list {
    max-height: 300px;
  }
  
  .actions-container {
    flex-direction: column;
    align-items: stretch;
  }
  
  .left-actions, .right-actions {
    justify-content: center;
    width: 100%;
  }
}
</style>