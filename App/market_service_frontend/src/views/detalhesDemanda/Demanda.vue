<script setup lang="ts">
import type { DemandaResponseInterface } from '@/types';
import { computed, ref } from 'vue';
import { onMounted } from 'vue';
import EditarDemandaForm from './EditarDemandaForm.vue';

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


  const podeEditar = computed(() => 
    props.tipoUsuario === 'CLIENTE' && props.clienteId === props.demanda.clienteId
  );

  const podeEnviarProposta = computed(() => 
    props.tipoUsuario === 'PRESTADOR'
  );

  const editando = ref(false); 
  
  const abrirFormEdicao = () => { editando.value = true; };

  const fecharFormEdicao = () => { editando.value = false; };

  const salvarEdicao = (demandaAtualizada: Partial<DemandaResponseInterface>) => {
    emit('atualizar-demanda', demandaAtualizada);
    editando.value = false;
  }

  
  onMounted(async () => {
    console.log("NO CARD", props.clienteId);

  });

</script>
<template>
  <v-card>
    <template v-if="!editando">
      <v-card-title>
        <strong>{{ props.demanda.titulo }}</strong>
      </v-card-title>
      <v-card-text>
        <p>{{ props.demanda.descricao }} </p>
      </v-card-text>
      <v-card-text>
        <p><strong>Localização:</strong> {{ props.demanda.localizacao }}</p>
        <p><strong>Categoria:</strong> {{ props.demanda.categoria }}</p>
        <p><strong>Status:</strong> {{ props.demanda.statusDemanda }}</p>
        <p><strong>Prioridade:</strong> {{ props.demanda.prioridade }}</p>
        <p><strong>Prazo:</strong> {{ props.demanda.prazo }}</p>
      </v-card-text>
      <v-card-actions>
        <v-btn
          v-if="podeEditar"
          color="primary"
          @click="abrirFormEdicao"
        >
          Editar Demanda
        </v-btn>
        <v-btn
          v-else-if="podeEnviarProposta"
          color="success"
          @click="$emit('proposta', props.demanda)"
        >
          Enviar Proposta
        </v-btn>
        <v-spacer></v-spacer>
        <v-btn text color="red" @click="$emit('fechar')">Fechar</v-btn>
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
  </v-card>
</template>