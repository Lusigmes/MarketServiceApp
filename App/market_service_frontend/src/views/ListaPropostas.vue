<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { carregarPropostasDoPrestador } from "@/api/PropostaService";
import { findPrestadorIdByUsuarioId } from "@/api/PrestadorService";
import type { PropostaResponseInterface } from "@/types";
import Proposta from "./detalhesProposta/Proposta.vue";
import { corStatusProposta } from "@/utils/labelsUtils";

  interface Props {
    usuario: any;
    usuarioId: number;
    tipoUsuario: 'CLIENTE' | 'PRESTADOR';
  }

  const props = defineProps<Props>();

  const prestadorId = ref<number | null>(null);

  const propostas = ref<PropostaResponseInterface[]>([]);
  const page = ref(0);
  const totalPages = ref(0);
  const loading = ref(false);

  const dialogDetalhe = ref(false);
  const propostaSelecionada = ref<PropostaResponseInterface | null>(null);
  
  const propostaAtual = computed(() => propostaSelecionada.value);

  const abrirModalDetalhe = (proposta: PropostaResponseInterface) => {
    propostaSelecionada.value = proposta;
    dialogDetalhe.value = true;
  };
  const fecharModalDetalhe = () => {
    dialogDetalhe.value = false;
    propostaSelecionada.value = null;
  };

  const atualizarProposta = (p: Partial<PropostaResponseInterface>) => {
    if (!p.id) return;
    const index = propostas.value.findIndex(item => item.id === p.id);
    if (index !== -1) propostas.value[index] = { ...propostas.value[index], ...p };
    if (propostaSelecionada.value?.id === p.id)
      propostaSelecionada.value = { ...propostas.value[index] };
  };

async function atualizarPagina(p: number = 0) {
  if (!prestadorId.value) return;
  loading.value = true;
  try {
    const data = await carregarPropostasDoPrestador(prestadorId.value, p, 9, "dataCriacaoProposta,DESC");
    propostas.value = data.content;
    totalPages.value = data.totalPages;
    page.value = p;
  } catch (err) {
    throw err;
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  try {
    prestadorId.value = await findPrestadorIdByUsuarioId(props.usuarioId);
    if (prestadorId.value) {
      await atualizarPagina();
    }
  } catch (err) {
      throw err;    
  }
});
</script>

<template>
  <v-container>
    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" />
    </v-row>

    <v-row v-else dense>
      <v-col
        v-for="proposta in propostas"
        :key="proposta.id"
        cols="12" sm="6" md="4"
      >
        <v-card
          class="pa-3 proposta-card"
          rounded="lg"
          elevation="4"
          :class="corStatusProposta(proposta.statusProposta)"
          @click="abrirModalDetalhe(proposta)"
          style="cursor: pointer; min-height: 160px;"
        >
          <div class="dark-overlay"></div>
          
          <div class="d-flex justify-space-between align-start mb-2">
            <div class="flex-grow-1 mr-2">
              <h3 class="text-h6 font-weight-bold text-white text-truncate" style="line-height: 1.3;">
                {{ proposta.titulo }}
              </h3>
            </div>
            
            <div class="preco-badge">
              <span class="text-white font-weight-bold text-caption">
                R$ {{ proposta.preco.toFixed(2) }}
              </span>
            </div>
          </div>

          <div class="d-flex align-center mb-3">
            <v-icon color="white" size="16" class="mr-1" style="opacity: 0.9;">
              mdi-comment-text
            </v-icon>
            <p class="text-body-2 text-white mb-0 text-truncate" style="opacity: 0.9; font-weight: 400;">
              {{ proposta.comentario?.substring(0, 50) }}{{ proposta.comentario?.length > 50 ? '...' : '' }}
            </p>
          </div>

          <div class="flex-grow-1 d-flex flex-column">
            <div class="mt-auto">
              <div class="d-flex flex-wrap gap-1 mb-2">
                <v-chip 
                  size="x-small" 
                  class="date-chip"
                  style="background: rgba(0, 0, 0, 0.3);"
                >
                  <v-icon size="10" color="white" class="mr-1">
                    mdi-calendar
                  </v-icon>
                  <span class="text-white font-weight-medium text-caption">
                    {{ new Date(proposta.dataCriacao).toLocaleDateString() }}
                  </span>
                </v-chip>
              </div>

              <div class="d-flex justify-space-between align-center">
                <div class="d-flex align-center">
                 
                </div>
                
                <v-chip 
                  size="x-small" 
                  class="status-chip"
                  style="background: rgba(0, 0, 0, 0.4);"
                >
                  <span class="text-caption text-white font-weight-bold" style="font-size: 0.7rem; letter-spacing: 0.3px;">
                    {{ proposta.statusProposta }}
                  </span>
                </v-chip>
              </div>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-row justify="center" class="mt-4">
      <v-pagination
        :length="totalPages"
        :model-value="page + 1"
        @update:model-value="(p) => atualizarPagina(p - 1)"
        color="primary"
        size="small"
        rounded
      />
    </v-row>

    <v-dialog v-model="dialogDetalhe" persistent max-width="600">
      <Proposta
        v-if="propostaAtual"
        :prestador-id="prestadorId"
        :tipo-usuario="props.tipoUsuario"
        :proposta="propostaAtual"
        @fechar="fecharModalDetalhe"
        @atualizar-proposta="atualizarProposta"
      />
    </v-dialog>
  </v-container>
</template>


<style scoped>
.proposta-card {
  position: relative;
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
}

.proposta-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.2) !important;
  z-index: 5;
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

.proposta-card > *:not(.dark-overlay) {
  position: relative;
  z-index: 2;
}

.preco-badge {
  position: relative;
  min-width: 60px;
  height: 28px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  flex-shrink: 0;
  padding: 0 8px;
}

.preco-badge span {
  color: #1a237e;
  font-size: 0.75rem;
}

.date-chip, .status-chip {
  transition: all 0.2s ease;
  border: none;
  backdrop-filter: blur(4px);
}

.date-chip:hover {
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