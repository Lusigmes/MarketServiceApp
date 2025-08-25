<script setup lang="ts">
import { reactive } from 'vue';
import type { DemandaResponseInterface } from '@/types';
import { PrioridadeDemanda, StatusDemanda } from '@/types/enums';
import { criarDemanda } from '@/api/DemandaService';

interface Props {
  clienteId: number | null;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  (e: 'salvar', payload: DemandaResponseInterface): void;
  (e: 'cancelar'): void;
}>();

const form = reactive<Partial<DemandaResponseInterface>>({
  titulo: '',
  descricao: '',
  categoria: '',
  localizacao: '',
  prazo: '',
  prioridade: PrioridadeDemanda.BAIXA,
  orcamentoEstimado: 0,
  clienteId: props.clienteId,
});

const salvar = async () => {
    try {
        const salvarDemanda = await criarDemanda(form);
        emit('salvar', salvarDemanda as DemandaResponseInterface);
    } catch (error) {
        console.error("ERRO: ", error)
    }
};
</script>

<template>
  <v-card>
    <v-card-title>Criar Demanda</v-card-title>
    <v-card-text style="max-height: 400px; overflow-y:auto;">
      <v-text-field v-model="form.titulo" label="Título" />
      <v-textarea v-model="form.descricao" label="Descrição" />
      <v-text-field v-model="form.categoria" label="Categoria" />
      <v-text-field v-model="form.localizacao" label="Localização" />
      <v-text-field v-model="form.prazo" label="Prazo" type="date" />
      <v-select v-model="form.prioridade" :items="Object.values(PrioridadeDemanda)" label="Prioridade" />
      <v-text-field v-model="form.orcamentoEstimado" label="Orçamento Estimado" type="number" />
    </v-card-text>
    <v-card-actions class="d-flex justify-end">
      <v-btn color="primary" @click="salvar">Salvar</v-btn>
      <v-btn text color="red" @click="$emit('cancelar')">Cancelar</v-btn>
    </v-card-actions>
  </v-card>
</template>
