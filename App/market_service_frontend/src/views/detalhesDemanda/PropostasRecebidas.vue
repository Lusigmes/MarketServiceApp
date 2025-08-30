<script setup lang="ts">
import type { PropostaResponseInterface } from '@/types';
import { StatusProposta } from '@/types/enums';
import { computed, onMounted, ref, watch } from 'vue';
import Proposta from '../detalhesProposta/Proposta.vue';

  interface Props {
      demandaId: number;
  }

  const emit = defineEmits<{
    (e: 'fechar'): void;
  }>();
  
  const props = defineProps<Props>();

  const propostas = ref<PropostaResponseInterface[]>([]);
  const propostaSelecionada = ref<PropostaResponseInterface | null>(null);
  const propostaAtual = computed(() => propostaSelecionada.value);

  const abrirDetalheProposta = (proposta: PropostaResponseInterface) => {
      propostaSelecionada.value = proposta;
  };
  
  const fecharDetalheProposta = () => {
      propostaSelecionada.value = null;
  };

  onMounted(() => {
    console.log("props.demandaid", props.demandaId)
    propostas.value = [
      { id: 1, titulo: 'Proposta 1', preco: 500, comentario: 'Posso fazer em 3 dias', statusProposta: StatusProposta.PENDENTE, dataCriacao: '2025-08-29', dataAtualizacao: '2025-08-29', demandaId: props.demandaId, prestadorId: 9 },
      { id: 2, titulo: 'Proposta 2', preco: 450, comentario: 'Entrega rápida', statusProposta: StatusProposta.PENDENTE, dataCriacao: '2025-08-28', dataAtualizacao: '2025-08-28', demandaId: props.demandaId, prestadorId: 9 },
      { id: 3, titulo: 'Proposta 3', preco: 450, comentario: 'Entrega rápida', statusProposta: StatusProposta.PENDENTE, dataCriacao: '2025-08-28', dataAtualizacao: '2025-08-28', demandaId: props.demandaId, prestadorId: 9 },
      { id: 4, titulo: 'Proposta 4', preco: 450, comentario: 'Entrega rápida', statusProposta: StatusProposta.PENDENTE, dataCriacao: '2025-08-28', dataAtualizacao: '2025-08-28', demandaId: props.demandaId, prestadorId: 9 },
      { id: 5, titulo: 'Proposta 5', preco: 450, comentario: 'Entrega rápida', statusProposta: StatusProposta.PENDENTE, dataCriacao: '2025-08-28', dataAtualizacao: '2025-08-28', demandaId: props.demandaId, prestadorId: 9 },
      { id: 6, titulo: 'Proposta 6', preco: 450, comentario: 'Entrega rápida', statusProposta: StatusProposta.PENDENTE, dataCriacao: '2025-08-28', dataAtualizacao: '2025-08-28', demandaId: props.demandaId, prestadorId: 9 },
    ];
    console.log("Mock propostas:", propostas.value);
  });


</script>


<template>
  <div>
    <v-list v-if="!propostaAtual">
      <v-list-item 
        v-for="prop in propostas" 
        :key="prop.id" 
        @click="abrirDetalheProposta(prop)"
        class="cursor-pointer"
      >
        <v-list-item-content>
          <v-list-item-title>{{ prop.titulo }}</v-list-item-title>
          <v-list-item-subtitle>
           Preço: R$ {{ prop.preco }} -  {{ prop.dataCriacao }}
          </v-list-item-subtitle>
        </v-list-item-content>
      </v-list-item>
    </v-list>

    <div v-else>
        <v-card class="pa-3">
          
          <Proposta/>

          <v-card-actions>
            <v-btn text color="primary" @click="fecharDetalheProposta">Aceitar</v-btn>
            <v-spacer></v-spacer>
            <v-btn text color="primary" @click="fecharDetalheProposta">Voltar</v-btn> 
          </v-card-actions>

        </v-card>
    </div>


    <v-card-actions>
      <v-spacer></v-spacer>
      
      <v-btn text color="red" @click="$emit('fechar')">
        Fechar
      </v-btn>
    </v-card-actions>
  </div>
</template>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
</style>