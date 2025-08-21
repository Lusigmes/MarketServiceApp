<script setup lang="ts">
import { onMounted } from 'vue';


    interface Props {
        demanda: any;
        tipoUsuario: 'CLIENTE' | 'PRESTADOR';
        usuarioId: number | undefined;
    }

    const props = defineProps<Props>();
  onMounted(() => {
  console.log(props.demanda);
  console.log(props.demanda.clienteId);
  console.log(props.usuarioId);
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
        v-if="props.tipoUsuario === 'CLIENTE' && Number(props.usuarioId) === Number(props.demanda.clienteId)"
        color="primary"
        @click="$emit('editar', props.demanda)"
      >
        Editar Demanda
      </v-btn>

      <v-btn
        v-else-if="props.tipoUsuario === 'PRESTADOR'"
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