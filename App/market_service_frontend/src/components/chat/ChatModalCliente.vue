<script setup lang="ts">
import { onMounted, ref } from 'vue';
import type { PrestadorResponseInterface } from '@/types';
import ChatCliente from './ChatCliente.vue';

interface Props {
  prestador: PrestadorResponseInterface;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'close'): void;
}>();

const dialog = ref(true);

const close = () => {
  dialog.value = false;
  setTimeout(() => emit('close'), 300);
};
onMounted(()=>{})
</script>


<template>
  <v-dialog v-model="dialog" max-width="500" persistent>
    <v-card rounded="lg">
      <v-card-title class="d-flex justify-space-between align-center bg-primary text-white">
        <span>Conversa direta com {{ prestador.usuario.nome }}</span>
        <v-btn icon @click="close" variant="text" color="white">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      
      <v-card-text class="pa-0">
        <ChatCliente :prestador="prestador" />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
