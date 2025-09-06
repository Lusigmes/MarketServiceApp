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

async function atualizarPagina(p: number = 0) {
  if (!prestadorId.value) return;
  loading.value = true;
  try {
    const data = await carregarPropostasDoPrestador(prestadorId.value, p, 6, "dataCriacaoProposta,DESC");
    propostas.value = data.content;
    totalPages.value = data.totalPages;
    page.value = p;
  } catch (err) {
    console.error("Erro ao carregar propostas:", err);
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
    console.error("Erro ao buscar prestadorId:", err);
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
          class="pa-3"
          :class="corStatusProposta(proposta.statusProposta)"
          rounded="lg"
          elevation="4"
          @click="abrirModalDetalhe(proposta)"
          style="cursor: pointer;"
        >
          <div class="d-flex justify-space-between align-center mb-2">
            <h3 class="text-h6 text-truncate">{{ proposta.titulo }}</h3>
            <v-chip size="small" color="indigo" text-color="white">
              R$ {{ proposta.preco.toFixed(2) }}
            </v-chip>
          </div>

          <p class="text-body-2 text-blue-darken-4 mb-2 text-truncate">
            {{ proposta.titulo }}
          </p>

          <div class="d-flex justify-space-between text-caption text-grey-darken-1">
            <span>Status: {{ proposta.statusProposta }}</span>
            <span>{{ new Date(proposta.dataCriacao).toLocaleDateString() }}</span>
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

    <v-dialog v-model="dialogDetalhe" max-width="600">
      <Proposta
        v-if="propostaAtual"
        :prestador-id="prestadorId"
        :proposta="propostaAtual"
        @fechar="fecharModalDetalhe"
      />
    </v-dialog>
  </v-container>
</template>
