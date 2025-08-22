<script setup lang="ts">
import { carregarDemandas } from '@/api/DemandaService';
import type { DemandaResponseInterface } from '@/types';
import { onMounted, ref } from 'vue';
import Demanda from './detalhes/Demanda.vue';

interface Props {
  usuario: any;
  usuarioId: number;
  tipoUsuario: 'CLIENTE' | 'PRESTADOR';
}

  const props = defineProps<Props>();

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

  const dialog = ref(false);
  const demandaSelecionada = ref<DemandaResponseInterface | null>(null);

  const abrirModal = (demanda: DemandaResponseInterface) => {
    demandaSelecionada.value = demanda;
    dialog.value = true;
  }

  const fecharModal = () => {
    dialog.value = false;
    demandaSelecionada.value = null;
  }

  onMounted(async ()=>{
      atualizarDemandas();
  });


</script>

<template>
  <v-container>
    
    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" class="my-4" />
    </v-row>

    <v-row v-else dense>
      <v-col
        v-for="demanda in demandas"
        :key="demanda.id"
        cols="12" sm="6" md="4"
      >
        <v-sheet
          rounded="lg"
          elevation="3"
          class="pa-1 demanda-card"
          @click="abrirModal(demanda)"
          style="cursor: pointer;"
        >
          <h3 class="text-primary mb-2">{{ demanda.titulo }}</h3>
          <p class="text-blue-darken-2 mb-2">{{ demanda.localizacao }}</p>

          <div class="d-flex flex-wrap gap-2">
            <v-chip color="blue-darken-4" text-color="blue-darken-4" size="small">
              {{ demanda.categoria }}
            </v-chip>
            <v-chip color="grey-darken-3" size="small">
              {{ demanda.statusDemanda }}
            </v-chip>
            <v-chip :class="getPrioridade(demanda.prioridade)" size="small" outlined>
              Prioridade: {{ demanda.prioridade }}
            </v-chip>
            <v-chip color="deep-purple-darken-4" size="small">
              Prazo: {{ demanda.prazo }}
            </v-chip>
          </div>
        </v-sheet>
      </v-col>
    </v-row>

    <v-dialog v-model="dialog" max-width="600">
      <template #default>
        <Demanda
          v-if="demandaSelecionada && props.usuario"
          :demanda="demandaSelecionada"
          :tipo-usuario="props.tipoUsuario"
          :usuario-id="props.usuarioId" 
          @fechar="fecharModal"
          @editar="(d: any) => console.log('Editar demanda', d)"
          @proposta="(d: any) => console.log('Enviar proposta', d)"
        />
      </template>
    </v-dialog>
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
.demanda-card {
  min-height: 150px; 
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
}

.demanda-card h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.demanda-card p {
  font-size: 0.950rem; 
  margin-bottom: 0.5rem;
}

.demanda-card .v-chip {
  font-size: 0.70rem; 
}
</style>
