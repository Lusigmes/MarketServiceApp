<script setup lang="ts">
import type { PropostaResponseInterface } from '@/types';
import { computed, onMounted, ref, watch } from 'vue';
import Proposta from '../detalhesProposta/Proposta.vue';
import { corStatusProposta } from '@/utils/labelsUtils';
import { carregarPropostasDaDemanda } from '@/api/PropostaService';
import { usePropostaPagination } from '@/composables/usePagination';
import { StatusProposta } from '@/types/enums';

  interface Props {
    demandaId: number;
    propostaAtualizadaProp: PropostaResponseInterface | null ;
    recarregar?: number;
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
      <!-- listagem -->
      <v-list v-if="!propostaAtual">
        <v-list-item 
          v-for="prop in propostas" 
          :key="prop.id" 
          @click="abrirDetalheProposta(prop)"
          class="cursor-pointer rounded mb-2"
          :class="corStatusProposta(prop.statusProposta)"
        >
          <v-list-item-title>{{ prop.titulo }}</v-list-item-title>
          <v-list-item-subtitle>
            Preço: R$ {{ prop.preco }} - {{ prop.dataCriacao }}
          </v-list-item-subtitle>
        </v-list-item>
      </v-list>

      <!-- paginação -->
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

      <div v-else-if="propostaAtual">
        <v-card class="pa-3">
          <Proposta :proposta="propostaAtual"  />

          <v-card-actions>
            <template v-if="propostaAtual && propostaAtual.statusProposta === StatusProposta.PENDENTE">
              <v-btn text color="green" @click="aceitarProposta">Aceitar</v-btn>
              <v-btn text color="red" @click="recusarProposta">Recusar</v-btn>
            </template>

            <template v-if="propostaAtual &&
                            propostaAtual.statusProposta === StatusProposta.ACEITA ||
                            propostaAtual.statusProposta === StatusProposta.RECUSADA ">
              <v-btn text color="orange" @click="desfazerProposta">Cancelar ação</v-btn>
            </template>

            <v-spacer></v-spacer>
            <v-btn text color="black" @click="fecharDetalheProposta">Voltar</v-btn>
          </v-card-actions>
        </v-card>
      </div>
    </template>

    <v-card-actions class="mt-3">
      <v-spacer></v-spacer>
      <v-btn text color="red" @click="$emit('fechar')">Fechar</v-btn>
    </v-card-actions>
  </div>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}
.cursor-pointer:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
  transform: translateY(-4px);
}
</style>