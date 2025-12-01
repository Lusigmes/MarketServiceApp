<template>
  <v-container>
    <v-row justify="center" v-if="loading">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="32" />
        <p class="text-body-2 text-medium-emphasis mt-2">Carregando prestadores...</p>
      </v-col>
    </v-row>

    <v-card v-else-if="prestadores.length > 0" variant="outlined" class="mb-4">
      <v-list>
        <v-list-item
          v-for="prestador in prestadores"
          :key="prestador.id"
          class="border-b"
          @click="abrirPerfil(prestador)"
          style="cursor: pointer;"
        >
          <template #prepend>
            <v-avatar color="primary" size="48" class="mr-4">
              <span class="text-white text-subtitle-1">
                {{ prestador.usuario.nome.charAt(0).toUpperCase() }}
              </span>
            </v-avatar>
          </template>

          <v-list-item-title class="text-h6 font-weight-medium text-primary">
            {{ prestador.usuario.nome }}
          </v-list-item-title>
          
          <v-list-item-subtitle class="text-body-1 mt-1">
            {{ prestador.usuario.email }} | {{ prestador.especializacao }}
          </v-list-item-subtitle>

          
          <template #append>
            <div class="d-flex align-center gap-2">
              <v-btn
                color="secondary"
                variant="flat"
                size="small"
                @click.stop="abrirChat(prestador)"
              >
                <v-icon small class="mr-1">mdi-chat</v-icon>
                Chat
              </v-btn>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </v-card>

    <v-card v-else variant="outlined" class="text-center py-8">
      <v-icon size="64" color="grey-lighten-1" class="mb-4">mdi-account-group</v-icon>
      <h3 class="text-h6 text-medium-emphasis mb-2">Nenhum prestador encontrado</h3>
      <p class="text-body-2 text-medium-emphasis">Não há prestadores de serviço cadastrados no momento.</p>
    </v-card>

    <v-row justify="center" class="mt-4" v-if="totalPages > 1">
      <v-col cols="auto">
        <v-pagination
          :length="totalPages"
          :model-value="page + 1"
          @update:model-value="mudarPagina"
          color="primary"
          size="small"
          rounded
        />
      </v-col>
    </v-row>

    <v-dialog v-model="dialogPerfil" max-width="600" persistent>
      <PerfilPrestador
        v-if="prestadorAtual"
        :prestador="prestadorAtual"
        @fechar="fecharPerfil"
        @abrirChat="abrirChat"
      />
    </v-dialog>
  </v-container>
</template>

<script setup lang="ts">
import { carregarPrestadoresPaginado } from '@/api/PrestadorService';
import { usePrestadorPagination } from '@/composables/usePagination';
import type { PrestadorResponseInterface } from '@/types';
import { computed, onMounted, ref } from 'vue';
import PerfilPrestador from './detallhesPrestadores/PerfilPrestador.vue';

interface Props {
  usuario: any;
  usuarioId: number;
  tipoUsuario: 'CLIENTE' | 'PRESTADOR';
}

const props = defineProps<Props>();

const {
  items: prestadores,
  page,
  totalPages,
  loading,
  atualizarPagina,
} = usePrestadorPagination<PrestadorResponseInterface>(carregarPrestadoresPaginado, 5, "usuario.nome,asc");

const dialogPerfil = ref(false);
const prestadorSelecionado = ref<PrestadorResponseInterface | null>(null);

const abrirPerfil = (prestador: PrestadorResponseInterface) => {
  prestadorSelecionado.value = prestador;
  dialogPerfil.value = true;
};

const fecharPerfil = () => {
  prestadorSelecionado.value = null;
  dialogPerfil.value = false;
};

const prestadorAtual = computed(() => prestadorSelecionado.value);

const mudarPagina = async (novaPagina: number) => {
  await atualizarPagina(novaPagina - 1);
};

const abrirChat = (prestador: PrestadorResponseInterface) => {
  console.log('Abrir chat com prestador:', prestador);
  // router.push(`/chat/${prestador.id}`);
};

onMounted(async () => {
  await atualizarPagina();
});
</script>

<style scoped>
.border-b {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}

.border-b:last-child {
  border-bottom: none;
}

.v-list-item {
  transition: background-color 0.2s ease;
}

.v-list-item:hover {
  background-color: rgba(0, 0, 0, 0.04);
}

:deep(.v-list-item__prepend) {
  align-items: center;
}

.text-primary {
  color: rgb(var(--v-theme-primary));
}

.text-primary:hover {
  opacity: 0.8;
}
</style>