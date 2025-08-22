<script setup lang="ts">
import { findClienteIdByUsuarioId } from '@/api/ClienteService';
import type { DemandaResponseInterface } from '@/types';
import { computed, ref } from 'vue';
import { onMounted } from 'vue';

  interface Props {
    demanda: DemandaResponseInterface;
    tipoUsuario: 'CLIENTE' | 'PRESTADOR';
    usuarioId: number;
  }
    
  const props = defineProps<Props>();
  const clienteId = ref<number | null>(null);
    

  const podeEditar = computed(() => 
    props.tipoUsuario === 'CLIENTE' && clienteId.value === props.demanda.clienteId
  );

  const podeEnviarProposta = computed(() => 
    props.tipoUsuario === 'PRESTADOR'
  );

  onMounted(async () => {
    try {
        clienteId.value = await findClienteIdByUsuarioId(props.usuarioId);
    }catch (error) {
        console.error('Erro ao buscar clienteId:', error);
    }
  });

</script>

<template>
  <v-card>
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
            @click="$emit('editar', props.demanda)"
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

      <v-btn text color="red" @click="$emit('fechar')">
        Fechar
      </v-btn>
    </v-card-actions>
  </v-card>
</template>