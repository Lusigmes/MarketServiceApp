<script setup lang="ts">
import type { DemandaResponseInterface } from '@/types';
import { computed, ref, onMounted } from 'vue';
import EditarDemandaForm from './EditarDemandaForm.vue';
import { corPrioridade, corStatus, labelPrioridade, labelStatus } from '@/utils/demandaLabels';
import PropostasRecebidas from './PropostasRecebidas.vue';

  interface Props {
    demanda: DemandaResponseInterface;
    tipoUsuario: 'CLIENTE' | 'PRESTADOR';
    usuarioId: number;
    clienteId: number | null;
  }
    
  const props = defineProps<Props>();

  const emit = defineEmits<{
    (e: 'atualizar-demanda', payload: Partial<DemandaResponseInterface>): void;
    (e: 'proposta', payload: DemandaResponseInterface): void;
    (e: 'fechar'): void;
  }>();


  const permissaoClienteResponsavel = computed(() => 
    props.tipoUsuario === 'CLIENTE' && props.clienteId === props.demanda.clienteId
  );

  const podeEnviarProposta = computed(() => 
    props.tipoUsuario === 'PRESTADOR' && props.demanda.statusDemanda === "ABERTA"
  );

  const editando = ref(false); 
  
  const abrirFormEdicao = () => { editando.value = true; };
  const fecharFormEdicao = () => { editando.value = false; };
  
  const salvarEdicao = (demandaAtualizada: Partial<DemandaResponseInterface>) => {
    emit('atualizar-demanda', demandaAtualizada);
    editando.value = false;
  }
  
  const abaSelecionada = ref("detalhes"); 

  onMounted(async () => {
    console.log("cliente no card", props.clienteId);
    console.log("demanda no CARD", props.demanda.id);
  });

</script>

<template>
  <v-card
    :color="corStatus(props.demanda.statusDemanda)"
    class="pa-4 rounded-lg elevation-4"
  >
    <v-tabs v-model="abaSelecionada" background-color="white" grow>
      <v-tab value="detalhes">Detalhes</v-tab>
      <v-tab value="propostas" v-if="permissaoClienteResponsavel">Propostas Recebidas</v-tab>
    </v-tabs>

    <v-window v-model="abaSelecionada" class="mt-3">     
      <v-window-item value="detalhes">
        <template v-if="!editando">
          <div class="d-flex justify-space-between align-center mt-2 mb-3">
            <h3 class="text-h6 text-truncate" style="max-width: 70%;">
            {{ props.demanda.titulo }}
          </h3>
          <v-chip
          :color="corPrioridade(props.demanda.prioridade)"
          class="white--text"
          small
          >
          Urgência:
          {{ labelPrioridade(props.demanda.prioridade) }}
          </v-chip>
        </div>

        <p class="mb-3" style="line-height: 1.4em;">
          {{ props.demanda.descricao }}
        </p>
        
        <div class="d-flex flex-wrap gap-2 mb-3">
          <v-chip small outlined>
            <strong>Status:</strong> {{ labelStatus(props.demanda.statusDemanda) }}
          </v-chip>
          <v-chip small outlined>
            <strong>Categoria:</strong> {{ props.demanda.categoria }}
          </v-chip>
          <v-chip small outlined>
            <strong>Localização:</strong> {{ props.demanda.localizacao }}
          </v-chip>
          <v-chip small outlined>
            <strong>Prazo Proposto:</strong> {{ props.demanda.prazo }}
          </v-chip>
        </div>

        <v-card-actions>
          <v-btn
            v-if="permissaoClienteResponsavel"
            color="primary"
            @click="abrirFormEdicao"
            >
            Editar
          </v-btn>
          
          <v-btn
            v-else-if="podeEnviarProposta"
            color="success"
            @click="$emit('proposta', props.demanda)"
          >
          Enviar Proposta
          </v-btn>
          
          <v-spacer></v-spacer>

          <v-btn text color="red" @click="$emit('fechar')">
            Fechar
          </v-btn>
        </v-card-actions>
      </template>

      <template v-else>
        <EditarDemandaForm
        :demanda="props.demanda"
          :cliente-id="props.clienteId"
          @cancelar="fecharFormEdicao"
          @salvar="salvarEdicao"
        />
      </template>
      </v-window-item>

      <v-window-item value="propostas">
        <PropostasRecebidas :demanda-id="props.demanda.id" @fechar="$emit('fechar')" />
      </v-window-item>
   </v-window>
  </v-card>
</template>

<style scoped>
.text-truncate {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>