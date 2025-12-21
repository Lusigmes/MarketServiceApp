<script setup lang="ts">
import { carregarPrestadoresPaginado } from '@/api/PrestadorService';
import { usePrestadorPagination } from '@/composables/usePagination';
import type { PrestadorResponseInterface } from '@/types';
import { computed, onMounted, ref } from 'vue';
import PerfilPrestador from './detallhesPrestadores/PerfilPrestador.vue';
import ChatModalCliente
 from '@/components/chat/ChatModalCliente.vue';
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

const mostrarChatModal = ref(false);
const prestadorSelecionadoChat = ref<PrestadorResponseInterface | null>(null);

const abrirChat = (prestador: PrestadorResponseInterface) => {
  mostrarChatModal.value = true;
  prestadorSelecionadoChat.value = prestador;
  console.log('Abrir chat com prestador:', prestador);
  // router.push(`/chat/${prestador.id}`);
};

const fecharChat = () => {
  mostrarChatModal.value = false;
  prestadorSelecionadoChat.value = null;
};

onMounted(async () => {
  await atualizarPagina();
});
</script>


<template>
  <v-container>
    <v-row justify="center" v-if="loading">
      <v-col cols="12" class="text-center">
        <v-progress-circular indeterminate color="primary" size="32" />
        <p class="text-body-2 text-medium-emphasis mt-2">Carregando prestadores...</p>
      </v-col>
    </v-row>

    <div v-else-if="prestadores.length > 0" class="prestadores-container">
      <v-card
        v-for="prestador in prestadores"
        :key="prestador.id"
        class="prestador-item mb-3"
        variant="outlined"
        @click="abrirPerfil(prestador)"
      >
        <div class="d-flex align-center pa-4">
          <v-avatar color="primary" size="56" class="mr-4">
            <span class="text-white text-subtitle-1 font-weight-bold">
              {{ prestador.usuario.nome.charAt(0).toUpperCase() }}
            </span>
          </v-avatar>

          <div class="flex-grow-1">
            <h3 class="text-h6 font-weight-bold text-primary mb-1">
              {{ prestador.usuario.nome }}
            </h3>
            <p class="text-body-2 text-medium-emphasis mb-1">
              {{ prestador.usuario.email }}
            </p>
            <div class="d-flex align-center gap-2">
              <v-chip size="x-small" color="primary" variant="outlined">
                {{ prestador.especializacao }}
              </v-chip>
            </div>
          </div>

          <div class="d-flex align-center gap-2">
            <v-btn
              color="secondary"
              variant="flat"
              size="small"
              @click.stop="abrirChat(prestador)"
              rounded="lg"
            >
              <v-icon size="16" class="mr-1">mdi-chat</v-icon>
              Chat
            </v-btn>
          </div>
        </div>
      </v-card>
    </div>

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
    <ChatModalCliente
    v-if="prestadorSelecionadoChat"
    :prestador="prestadorSelecionadoChat"
    @close="fecharChat"
  />
  </v-container>
</template>

<style scoped>
.prestadores-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.prestador-item {
  border-radius: 12px !important;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.12);
  overflow: hidden;
}

.prestador-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1) !important;
  border-color: rgba(25, 118, 210, 0.3);
  background-color: rgba(227, 242, 253, 0.1);
}

.prestador-item:active {
  transform: translateY(0);
}

.text-primary {
  color: rgb(var(--v-theme-primary));
}

.v-avatar {
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.2);
}
</style>