<script setup lang="ts">
import { carregarDemandas } from '@/api/DemandaService';
import type { DemandaResponseInterface } from '@/types';
import { onMounted, ref } from 'vue';
import Demanda from './detalhesDemanda/Demanda.vue';
import { PrioridadeDemanda } from '@/types/enums';
import CriarDemandaForm from './detalhesDemanda/CriarDemandaForm.vue';
import { findClienteIdByUsuarioId } from '@/api/ClienteService';

  interface Props {
    usuario: any;
    usuarioId: number;
    tipoUsuario: 'CLIENTE' | 'PRESTADOR';
  }

  const props = defineProps<Props>();

  const clienteId = ref<number | null>(null);

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
  
  const getPrioridade = (prioridade: PrioridadeDemanda) => {
    switch (prioridade) {
      case PrioridadeDemanda.BAIXA:
        return 'text-green-darken-4';
        case PrioridadeDemanda.MEDIA:
          return 'text-yellow-darken-4';
          case PrioridadeDemanda.ALTA:
      return 'text-red-darken-4';
      default:
      return null;
    }
  };

  const dialogDetalhe = ref(false);
  const demandaSelecionada = ref<DemandaResponseInterface | null>(null);
  
  
  
  const abrirModalDetalhe = (demanda: DemandaResponseInterface) => {
    demandaSelecionada.value = demanda;
    dialogDetalhe.value = true;
  }

  const fecharModalDetalhe = () => {
    dialogDetalhe.value = false;
    demandaSelecionada.value = null;
  }

  const atualizarDemanda = (d: Partial<DemandaResponseInterface>) => {
    if (!d.id) return; 
    const index = demandas.value.findIndex(item => item.id === d.id);
    if(index !== -1){
      demandas.value[index] = { ...demandas.value[index], ...d };    
    }

    if (demandaSelecionada.value?.id === d.id) {
      demandaSelecionada.value = { ...demandas.value[index] };
    }
  };
  
  const dialogCriacao = ref(false);

  const abrirModalCriacao = () => {
    dialogCriacao.value = true;
  }
  
  const fecharModalCriacao = () => {
    dialogCriacao.value = false;
  }

  onMounted(async () => {
    try {
        clienteId.value = await findClienteIdByUsuarioId(props.usuarioId);
        atualizarDemandas();
    }catch (error) {
        console.error('Erro ao buscar clienteId:', error);
    }
  });
</script>

<template>
  <v-container>
    <v-row justify="end" class="mb-4">
      <v-btn color="primary" @click="abrirModalCriacao">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </v-row>

    <v-row justify="center" v-if="loading">
      <v-progress-circular indeterminate color="primary" class="my-4" />
    </v-row>
    
    <v-row v-else dense>
      <v-col
        v-for="demanda in demandas"
        :key="demanda.id"
        cols="12" sm="6" md="4" lg="4"
      >
        <v-sheet
          rounded="lg"
          elevation="3"
          class="pa-1 demanda-card"
          @click="abrirModalDetalhe(demanda)"
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
          </div>
        </v-sheet>
      </v-col>
    </v-row>

    <v-dialog v-model="dialogDetalhe" max-width="600" persistent>
      <template #default>
        <Demanda
          v-if="demandaSelecionada && props.usuario"
          :demanda="demandaSelecionada"
          :tipo-usuario="props.tipoUsuario"
          :usuario-id="props.usuarioId" 
          :cliente-id="clienteId"
          @fechar="fecharModalDetalhe"
          @atualizar-demanda="atualizarDemanda"
          @proposta="(d: any) => console.log('Enviar proposta', d)"
        />
      </template>
    </v-dialog>

    <v-dialog v-model="dialogCriacao" max-width="600" persistent>
      <template #default>
        <CriarDemandaForm
          :cliente-id="clienteId"
          @cancelar="fecharModalCriacao"
          @salvar="(d: DemandaResponseInterface) => { 
            demandas.push(d); 
            fecharModalCriacao(); 
          }"
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

.fab-btn {
  position: fixed;
  top: 0px; 
  right: 0px;  
  z-index: 1000;
}

</style>
