<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const menuOpen = ref(false);

const currentTab = computed(() => {
  const currentRoute = router.currentRoute.value.path;
  if (currentRoute === '/dashboard' || currentRoute.startsWith('/dashboard/demandas')) return 'demandas';
  if (currentRoute === '/dashboard/prestadores') return 'prestadores';
  if (currentRoute === '/dashboard/propostas') return 'propostas';
  return null;
});

const showInfo = computed(() => {
  return currentTab.value && ['demandas', 'propostas', 'prestadores'].includes(currentTab.value);
});

const closeInfo = () => {
  menuOpen.value = false;
};
</script>

<template>
  <v-menu
    location="bottom"
    :close-on-content-click="false"
    v-if="showInfo"
    v-model="menuOpen"
  >
    <template #activator="{ props }">
      <v-btn
        v-bind="props"
        icon
        size="small"
        color="white"
        variant="text"
        class="info-btn"
      >
        <v-icon>mdi-information</v-icon>
      </v-btn>
    </template>

    <v-card width="400" class="info-card">
      <v-card-title class="d-flex justify-space-between align-center">
        <span v-if=" currentTab === 'demandas' ||  currentTab === 'propostas' " class="text-h6">Significado das cores nos STATUS</span>
        <span v-else-if="currentTab === 'prestadores'" class="text-h6">Prestadores disponíveis</span>
        <v-btn icon size="small" @click="closeInfo" elevation="0">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      
      <v-card-text>
        <!-- demandas -->
        <div v-if="currentTab === 'demandas'" class="info-section">
          <h4 class="text-subtitle-1 font-weight-bold mb-2">Status das Demandas</h4>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-blue-lighten-4"></div>
            <span class="ml-2">Aberta - Recebendo propostas</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-amber-lighten-4"></div>
            <span class="ml-2">Em Andamento - Proposta aceita</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-green-lighten-4"></div>
            <span class="ml-2">Concluída - Serviço finalizado</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-red-lighten-4"></div>
            <span class="ml-2">Cancelada - Demanda cancelada</span>
          </div>
          
          <v-divider class="my-3"></v-divider>
          
          <h4 class="text-subtitle-1 font-weight-bold mb-2">Status das Propostas</h4>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-blue-lighten-4"></div>
            <span class="ml-2">Pendente - Aguardando análise</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-amber-lighten-4"></div>
            <span class="ml-2">Aceita - Proposta selecionada</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-green-lighten-4"></div>
            <span class="ml-2">Concluída - Serviço realizado</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-red-lighten-4"></div>
            <span class="ml-2">Recusada - Proposta não selecionada</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-red-darken-4"></div>
            <span class="ml-2">Cancelada - Cancelada pelo prestador</span>
          </div>
        </div>

        <!-- pPropostas -->
        <div v-else-if="currentTab === 'propostas'" class="info-section">
          <h4 class="text-subtitle-1 font-weight-bold mb-2">Status das Propostas</h4>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-blue-lighten-4"></div>
            <span class="ml-2">Pendente - Aguardando análise do cliente</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-amber-lighten-4"></div>
            <span class="ml-2">Aceita - Demanda em andamento</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-green-lighten-4"></div>
            <span class="ml-2">Concluída - Serviço finalizado com sucesso</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-red-lighten-4"></div>
            <span class="ml-2">Recusada - Não selecionada pelo cliente</span>
          </div>
          <div class="legend-item mb-2">
            <div class="color-indicator bg-red-darken-4"></div>
            <span class="ml-2">Cancelada - Cancelada por você</span>
          </div>
        </div>

        <!-- prestadores -->
        <div v-else-if="currentTab === 'prestadores'" class="info-section">
          <h4 class="text-subtitle-1 font-weight-bold mb-2">Informações sobre Prestadores</h4>
          <p class="text-body-2 mb-3">
            Visualize prestadores disponíveis para atender suas demandas. 
            Cada prestador possui uma avaliação média baseada no histórico de serviços.
          </p>
          <div class="legend-item mb-2">
            <v-icon color="orange" class="mr-2">mdi-star</v-icon>
            <span>Avaliação - Média de 1 a 5 estrelas</span>
          </div>
        </div>
      </v-card-text>
    </v-card>
  </v-menu>
</template>

<style scoped>
.info-btn {
  opacity: 0.8;
  transition: opacity 0.3s ease;
}

.info-btn:hover {
  opacity: 1;
  transform: scale(1.1);
}

.info-card {
  border-radius: 12px !important;
  border: 1px solid #e0e0e0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15) !important;
}

.info-section {
  padding: 8px 0;
}

.legend-item {
  display: flex;
  align-items: center;
  padding: 4px 0;
}

.color-indicator {
  width: 20px;
  height: 20px;
  border-radius: 4px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}
</style>