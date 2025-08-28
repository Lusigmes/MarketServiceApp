<script setup lang="ts">
import { carregarDemandas } from '@/api/DemandaService';
import type { DemandaResponseInterface } from '@/types';
import { onMounted, ref } from 'vue';
import Demanda from './detalhesDemanda/Demanda.vue';
import CriarDemandaForm from './detalhesDemanda/CriarDemandaForm.vue';
import { findClienteIdByUsuarioId } from '@/api/ClienteService';
import { corStatus, corPrioridade } from '@/utils/demandaLabels';
import { usePagination } from '@/composables/usePagination';

  interface Props {
    usuario: any;
    usuarioId: number;
    tipoUsuario: 'CLIENTE' | 'PRESTADOR';
  }
  const props = defineProps<Props>();

  const clienteId = ref<number | null>(null);
  
  const {
    items: demandas,
    page, totalPages,
    loading, atualizarPagina 
  } = usePagination<DemandaResponseInterface>(carregarDemandas, 9);

  const dialogDetalhe = ref(false);
  const demandaSelecionada = ref<DemandaResponseInterface | null>(null);

  const abrirModalDetalhe = (demanda: DemandaResponseInterface) => {
    demandaSelecionada.value = demanda;
    dialogDetalhe.value = true;
  };
  const fecharModalDetalhe = () => {
    dialogDetalhe.value = false;
    demandaSelecionada.value = null;
  };

  const atualizarDemanda = (d: Partial<DemandaResponseInterface>) => {
    if (!d.id) return;
    const index = demandas.value.findIndex(item => item.id === d.id);
    if (index !== -1) demandas.value[index] = { ...demandas.value[index], ...d };
    if (demandaSelecionada.value?.id === d.id)
      demandaSelecionada.value = { ...demandas.value[index] };
  };

  const dialogCriacao = ref(false);

  const mudarPagina = async (novaPagina: number) => {
    await atualizarPagina(novaPagina-1);
  };

  const salvarDemanda = async (d: DemandaResponseInterface) => {
    demandas.value.push(d);
    dialogCriacao.value = false;
    await atualizarPagina(page.value);
  }

  onMounted(async () => {
    try {
      if(props.tipoUsuario === "CLIENTE"){
        clienteId.value = await findClienteIdByUsuarioId(props.usuarioId);
      }
      await atualizarPagina();
    } catch (error) {
      console.error('Erro ao buscar clienteId:', error);
    }
  });

</script>

<template>
  <v-container>
    <v-row justify="end" class="mb-4 mr-1">
      <v-btn
  color="primary"
  prepend-icon="mdi-plus"
  class="px-4"
  @click="dialogCriacao = true"
>
  Nova Demanda
</v-btn>
    </v-row>

    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" />
    </v-row>

    <v-row v-else dense>
      <v-col
        v-for="demanda in demandas"
        :key="demanda.id"
        cols="12" sm="6" md="4"
      >
        <v-card
          class="pa-3"
          rounded="lg"
          elevation="4"
          :color="corStatus(demanda.statusDemanda)"
          @click="abrirModalDetalhe(demanda)"
          style="cursor: pointer;"
        >
          <div class="d-flex justify-space-between align-center mb-2">
            <h3 class="text-h6 text-truncate">{{ demanda.titulo }}</h3>
            <v-icon :color="corPrioridade(demanda.prioridade)" size="20">
              mdi-alert
            </v-icon>
          </div>

          <p class="text-body-2 text-blue-darken-4 mb-2 text-truncate">
            {{ demanda.localizacao }}
          </p>

          <div class="d-flex flex-wrap gap-2">
            <v-chip size="small" variant="outlined" color="indigo-darken-4">
              {{ demanda.categoria }}
            </v-chip>
            <v-chip size="small" variant="outlined" color="grey-darken-4">
              Prazo: {{ demanda.prazo }}
            </v-chip>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-row justify="center" class="mt-4">
      <v-pagination
        :length="totalPages"
        :model-value="page + 1"     
        @update:model-value="mudarPagina"
        color="primary"
        size="small"
        rounded
      />
    </v-row>

    <v-dialog v-model="dialogDetalhe" max-width="600" persistent>
      <Demanda
        v-if="demandaSelecionada && props.usuario"
        :demanda="demandaSelecionada"
        :tipo-usuario="props.tipoUsuario"
        :usuario-id="props.usuarioId"
        :cliente-id="clienteId"
        @fechar="fecharModalDetalhe"
        @atualizar-demanda="atualizarDemanda"
      />
    </v-dialog>

    <v-dialog v-model="dialogCriacao" max-width="600" persistent>
      <CriarDemandaForm
        :cliente-id="clienteId"
        @cancelar="dialogCriacao = false"
        @salvar="salvarDemanda"
        />
    </v-dialog>
  </v-container>
</template>
