<script setup lang="ts">
import { carregarDemandas, carregarDemandasDoCliente } from '@/api/DemandaService';
import type { DemandaResponseInterface } from '@/types';
import { computed, onMounted, ref, watch } from 'vue';
import Demanda from './detalhesDemanda/Demanda.vue';
import CriarDemandaForm from './detalhesDemanda/CriarDemandaForm.vue';
import { findClienteIdByUsuarioId } from '@/api/ClienteService';
import { corStatus, corPrioridade, labelStatusDemanda } from '@/utils/labelsUtils';
import { useDemandaPagination } from '@/composables/usePagination';

interface Props {
  usuario: any;
  usuarioId: number;
  tipoUsuario: 'CLIENTE' | 'PRESTADOR';
}

const props = defineProps<Props>();

const clienteId = ref<number | null>(null);

const tipoDeCarregarDemandas = computed(() => {
  if (props.tipoUsuario === 'CLIENTE') {
    return carregarDemandasDoCliente;
  } else {
    return carregarDemandas;
  }
});

const permissaoCliente = computed(() => props.tipoUsuario === 'CLIENTE');

const {
  items: demandas,
  page,
  totalPages,
  loading,
  atualizarPagina
} = useDemandaPagination<DemandaResponseInterface>(tipoDeCarregarDemandas.value, 9);

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
  await atualizarPagina(novaPagina - 1);
};

const salvarDemanda = async (d: DemandaResponseInterface) => {
  if (props.tipoUsuario === 'CLIENTE') {
    demandas.value.unshift(d);
  } else {
    demandas.value.push(d);
  }
  dialogCriacao.value = false;
  await atualizarPagina(page.value);
};

watch(() => props.tipoUsuario, () => {
  atualizarPagina(0);
});

onMounted(async () => {
  try {
    if (props.tipoUsuario === "CLIENTE") {
      clienteId.value = await findClienteIdByUsuarioId(props.usuarioId);
    }
    await atualizarPagina();
  } catch (error) {
    console.error("Erro ao carregar demandas:", error);
  }
});
</script>

<template>
  <v-container>
    <v-row justify="end" align="center" class="mb-4">      
      <v-col cols="auto">
        <v-btn
          color="primary"
          class="px-4"
          @click="dialogCriacao = true"
          v-if="permissaoCliente"
        >
          Nova Demanda
        </v-btn>
      </v-col>
    </v-row>

    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" size="64" width="4" />
    </v-row>

    <v-row v-else-if="demandas.length > 0" dense>
      <v-col
        v-for="demanda in demandas"
        :key="demanda.id"
        cols="12" sm="6" md="4"
      >
        <v-card
          class="pa-3 demanda-card"
          rounded="lg"
          elevation="4"
          :color="corStatus(demanda.statusDemanda)"
          @click="abrirModalDetalhe(demanda)"
          style="cursor: pointer; min-height: 160px; position: relative;"
        >          
          <div class="dark-overlay"></div>
          
          <div class="d-flex justify-space-between align-start mb-2">
            <div class="flex-grow-1 mr-2">
              <h3 class="text-h6 font-weight-bold text-white text-truncate" style="line-height: 1.3;">
                {{ demanda.titulo }}
              </h3>
            </div>
            
            <div class="priority-badge">
              <v-icon :color="corPrioridade(demanda.prioridade)" size="18">
                mdi-flag
              </v-icon>
            </div>
          </div>

          <div class="d-flex align-center mb-3">
            <v-icon color="white" size="16" class="mr-1" style="opacity: 0.9;">
              mdi-map-marker
            </v-icon>
            <p class="text-body-2 text-white mb-0 text-truncate" style="opacity: 0.9; font-weight: 400;">
              {{ demanda.localizacao }}
            </p>
          </div>

          <div class="flex-grow-1 d-flex flex-column">
            <div class="mt-auto">
              <div class="d-flex flex-wrap gap-1 mb-2">
                <v-chip 
                  size="x-small" 
                  class="category-chip"
                  style="background: rgba(0, 0, 0, 0.3);"
                >
                  <span class="text-white font-weight-medium text-caption">
                    {{ demanda.categoria }}
                  </span>
                </v-chip>
              </div>

              <div class="d-flex justify-space-between align-center">
                <div class="d-flex align-center">
                  <v-icon size="12" color="white" class="mr-1" style="opacity: 0.8;">
                    mdi-calendar
                  </v-icon>
                  <span class="text-caption text-white font-weight-medium">
                    {{ demanda.prazo }}
                  </span>
                </div>
                
                <v-chip 
                  size="x-small" 
                  class="status-chip"
                  style="background: rgba(0, 0, 0, 0.4);"
                >
                  <span class="text-caption text-white font-weight-bold" style="font-size: 0.7rem; letter-spacing: 0.3px;">
                    {{ labelStatusDemanda(demanda.statusDemanda)}}
                  </span>
                </v-chip>
              </div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-row v-else justify="center" class="mt-8">
      <v-col cols="12" class="text-center">
        <v-icon size="64" color="grey-lighten-2" class="mb-4">
          mdi-clipboard-text-outline
        </v-icon>
        <h3 class="text-h6 text-grey-darken-1 mb-2">
          {{
            permissaoCliente 
              ? "Você ainda não criou nenhuma demanda" 
              : "Nenhuma demanda disponível no momento"
          }}
        </h3>
        <p class="text-body-2 text-grey">
          {{
            permissaoCliente
              ? "Clique no botão 'Nova Demanda' para criar sua primeira demanda"
              : "Aguarde até que algum cliente crie uma nova demanda"
          }}
        </p>
      </v-col>
    </v-row>

    <v-row justify="center" class="mt-4" v-if="demandas.length > 0">
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

<style scoped>
.demanda-card {
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.demanda-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2) !important;
  z-index: 5;
}

.demanda-owner-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  z-index: 3;
}

.owner-chip {
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.dark-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.25));
  pointer-events: none;
  z-index: 1;
}

.demanda-card > *:not(.dark-overlay):not(.demanda-owner-badge) {
  position: relative;
  z-index: 2;
}

.demanda-card .flex-grow-1 {
  min-width: 0;
}

.priority-badge {
  position: relative;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  flex-shrink: 0;
}

.category-chip, .date-chip, .status-chip {
  transition: all 0.2s ease;
  border: none;
  backdrop-filter: blur(4px);
}

.category-chip:hover, .date-chip:hover {
  background: rgba(0, 0, 0, 0.4) !important;
}

.text-white {
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
}

.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
