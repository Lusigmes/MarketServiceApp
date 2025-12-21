<script setup lang="ts">
import { findPrestadorIdByUsuarioId } from '@/api/PrestadorService';
import { useAuth } from '@/composables/useAuth';
import { useNotification } from '@/composables/useNotification';
import { ref } from 'vue';
import MinhasConversasPrestador from './MinhasConversasPrestador.vue';

const { usuario } = useAuth();

const dialog = ref(false);
const prestadorId = ref<number | null>(null);
const loading = ref(false);

const { showNotification } = useNotification();

const abrir = async () => {
    dialog.value = true;
    await carregarPrestadorId();
};

const fechar = () => {
    dialog.value = false;
};

const carregarPrestadorId = async () => {
    if (!usuario.value?.id) return;
    
    loading.value = true;
    try {
        prestadorId.value = await findPrestadorIdByUsuarioId(usuario.value.id);
    } catch (error) {
        showNotification('Erro ao carregar ID do prestador', "error");
    } finally {
        loading.value = false;
    }
};

defineExpose({
    abrir, fechar
});
</script>
<template>
  <v-dialog 
    v-model="dialog" 
    max-width="600px"
    persistent
    scrollable
    :fullscreen="$vuetify.display.xs" 
    class="conversas-modal"
  >
    <v-card>
      <v-toolbar color="primary" density="compact">
        <v-toolbar-title class="text-white">
          Minhas Conversas
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn
          icon
          color="white"
          @click="fechar"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-toolbar>

      <v-card-text class="pa-0"> 
        <div v-if="loading" class="text-center py-8">
          <v-progress-circular indeterminate color="primary" size="32" />
          <p class="text-body-1 mt-2">Carregando suas conversas...</p>
        </div>

        <div v-else-if="prestadorId" class="h-100">
          <div style="height: 500px; max-height: 70vh;" class="d-flex flex-column">
            <MinhasConversasPrestador :prestador-id="prestadorId" />
          </div>
        </div>

        <div v-else class="text-center py-8">
          <v-icon size="48" color="grey-lighten-2" class="mb-2">mdi-alert-circle</v-icon>
          <p class="text-body-1">Não foi possível carregar suas conversas.</p>
          <v-btn 
            color="primary" 
            variant="text" 
            @click="carregarPrestadorId"
            class="mt-2"
          >
            Tentar novamente
          </v-btn>
        </div>
      </v-card-text>

      <v-card-actions class="pa-4">
        <v-spacer></v-spacer>
        <v-btn
          color="primary"
          variant="flat"
          @click="fechar"
        >
          Fechar
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>