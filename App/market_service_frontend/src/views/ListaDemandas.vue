<script setup lang="ts">
    import { carregarDemandas } from '@/api/DemandaService';
    import type { DemandaResponseInterface } from '@/types';
    import { onMounted, ref } from 'vue';

    const demandas = ref<DemandaResponseInterface[]>([]);
    const loading = ref(true);

    const atualizarDemandas = async () => {
        try {
            demandas.value = await carregarDemandas();
        } catch (error) {
            throw error;
        }finally{
            loading.value = false;
        }
    };
    const getPrioridade = (prioridade: string) => {
        switch (prioridade.toLowerCase()) {
            case 'baixa':
            return 'text-green-darken-4';
            case 'media':
            return 'text-yellow-darken-4';
            case 'alta':
            return 'text-red-darken-4';
            default:
            return '';
    }
};

    onMounted(async ()=>{
        atualizarDemandas();
    });


</script>
<template>
  <v-container>
    <!-- Loading -->
    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" class="my-4" />
    </v-row>

    <!-- Grade -->
    <v-row v-else dense>
      <v-col v-for="demanda in demandas" :key="demanda.id" cols="12" sm="6" md="4">
        <v-sheet rounded="lg" elevation="2" class="pa-4">
          <h3 class="text-primary mb-2">{{ demanda.titulo }}</h3>
          <p class="text-blue-darken-2 mb-2">{{ demanda.localizacao }}</p>

          <div class="d-flex flex-wrap gap-2">
            <v-chip color="blue-lighten-4" text-color="blue-darken-4" size="small">
              {{ demanda.categoria }}
            </v-chip>
            <v-chip color="grey-lighten-3" size="small">
              {{ demanda.statusDemanda }}
            </v-chip>
            <v-chip :class="getPrioridade(demanda.prioridade)" size="small" outlined>
              Prioridade: {{ demanda.prioridade }}
            </v-chip>
            <v-chip color="deep-purple-lighten-4" size="small">
              Prazo: {{ demanda.prazo }}
            </v-chip>
          </div>
        </v-sheet>
      </v-col>
    </v-row>
  </v-container>
</template>

<style scoped>
.v-list-item {
  border: 1px solid rgba(0, 0, 0, 0.08);
  border-radius: 8px;
}
.v-list-item-subtitle.d-flex {
  flex-wrap: wrap;
  gap: 6px;
  align-items: center;
}
</style>
