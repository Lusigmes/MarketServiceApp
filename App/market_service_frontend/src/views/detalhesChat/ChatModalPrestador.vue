<script setup lang="ts">
import { ref } from 'vue';
import ChatPrestador from './ChatPrestador.vue';

interface Props {
  clienteId?: number;
  clienteNome?: string | null;
  cliente?: any;
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
</script>

<template>
  <v-dialog v-model="dialog" max-width="500" persistent>
    <v-card rounded="lg">
      <v-card-title class="d-flex justify-space-between align-center bg-primary text-white">
        <span>Conversa direta com {{ clienteNome || 'Cliente' }}</span>
        <v-btn icon @click="close" variant="text" color="white">
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>
      
      <v-card-text class="pa-0">
        <ChatPrestador 
          v-if="clienteId"
          :cliente-id="clienteId" 
          @close="close" 
        />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>