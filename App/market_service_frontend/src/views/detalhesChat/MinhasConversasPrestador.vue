<script setup lang="ts">
import { buscarConversasPrestador } from '@/api/ChatService';
import { findPrestadorIdByUsuarioId } from '@/api/PrestadorService';
import { useAuth } from '@/composables/useAuth';
import type { ChatConversationInterface } from '@/types';
import { formatarDataParaExibicao } from '@/utils/dateUtils';
import { onMounted, ref } from 'vue';
import ChatPrestador from './ChatPrestador.vue';
import ChatModalPrestador from './ChatModalPrestador.vue';


const { usuario } = useAuth();

const conversas = ref<ChatConversationInterface[]>([]);
const loading = ref(false);
const error = ref<string | null>(null);
  
  
const dialogChat = ref(false);
const clienteIdSelecionado = ref<number | null>(null);
const clienteNomeSelecionado = ref('');

const carregarConversas = async () => {
  if(!usuario.value || usuario.value.tipoUsuario !== "PRESTADOR") return

  loading.value = true;
  error.value = null;
  
  try {
    const prestadorId = await findPrestadorIdByUsuarioId(usuario.value.id);
    conversas.value = await buscarConversasPrestador(prestadorId);
  }  catch (err: any) {
    error.value = err.message || 'Erro ao carregar conversas';
    console.error('Erro:', err);
  } finally {
    loading.value = false;
  }

};

const formatarUltimaMensagem = (mensagem: string) => {
  if(!mensagem) return 'Sem mensagens';
  if(mensagem.length > 50) return mensagem.substring(0, 50) + '...';
  return mensagem;
};

const formatarData = (dataString: string) => {
  const data = new Date(dataString);
  const agora = new Date();
  const diff = agora.getTime() - data.getTime();
  const diffDias = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (diffDias === 0) {
    return data.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
  } else if (diffDias === 1) {
    return 'Ontem';
  } else if (diffDias < 7) {
    return `${diffDias} dias atrás`;
  } else {
    return formatarDataParaExibicao(dataString);
  }
};

const abrirChat = (clienteId: number, clienteNome: string) => {
  clienteIdSelecionado.value = clienteId;
  clienteNomeSelecionado.value = clienteNome;
  dialogChat.value = true;
};

const fecharChat = () => {
  dialogChat.value = false;
  clienteIdSelecionado.value = null;
  clienteNomeSelecionado.value = '';
  carregarConversas();
};

onMounted(async () => {
  if(usuario.value?.tipoUsuario === 'PRESTADOR'){
    await carregarConversas();
  }
});

</script>

<template>
  <div class="h-100 d-flex flex-column">
    <div class="pa-4 pb-2 border-b">
      <div class="d-flex align-center justify-space-between">
        <div>
          <h3 class="text-h6 font-weight-bold mb-1">Clientes interessados</h3>
        </div>
        <v-chip color="primary" variant="flat" size="small">
          {{ conversas.length }}
        </v-chip>
      </div>
    </div>

    <div v-if="loading" class="flex-grow-1 d-flex align-center justify-center py-8">
      <div class="text-center">
        <v-progress-circular indeterminate color="primary" size="32" />
        <p class="text-body-2 text-medium-emphasis mt-2">Carregando...</p>
      </div>
    </div>

    <div v-else-if="error" class="pa-4">
      <v-alert type="error" variant="tonal" density="compact">
        {{ error }}
        <v-btn @click="carregarConversas" variant="text" size="small" class="mt-1">Tentar novamente</v-btn>
      </v-alert>
    </div>

    <div v-else-if="conversas.length === 0" class="flex-grow-1 d-flex align-center justify-center">
      <div class="text-center pa-4">
        <v-icon size="48" color="grey-lighten-2" class="mb-2">mdi-chat-outline</v-icon>
        <h3 class="text-h6 text-medium-emphasis">Nenhuma conversa</h3>
        <p class="text-caption text-medium-emphasis">
          Quando um cliente iniciar uma conversa, ela aparecerá aqui.
        </p>
      </div>
    </div>

    <div v-else class="flex-grow-1 overflow-y-auto">
      <div class="pa-2">
        <v-list class="py-0" density="compact">
          <v-list-item
            v-for="conversa in conversas"
            :key="conversa.clienteId"
            @click="abrirChat(conversa.clienteId, conversa.clienteNome)"
            class="mb-1 conversa-item"
            :class="{ 'has-unread': !conversa.lida && !conversa.enviadoPorCliente }"
          >
            <template #prepend>
              <v-avatar color="primary" size="36">
                <span class="text-white text-body-2">
                  {{ conversa.clienteNome.charAt(0).toUpperCase() }}
                </span>
              </v-avatar>
            </template>

            <v-list-item-title class="text-body-2 font-weight-medium">
              <div class="d-flex justify-space-between align-center">
                <span>{{ conversa.clienteNome }}</span>
                <span class="text-caption text-medium-emphasis">
                  {{ formatarData(conversa.dataUltimaMensagem) }}
                </span>
              </div>
            </v-list-item-title>

            <v-list-item-subtitle class="text-caption">
              <div class="d-flex justify-space-between align-center">
                <span class="text-truncate" style="max-width: 200px;">
                  {{ formatarUltimaMensagem(conversa.ultimaMensagem) }}
                </span>
                <v-badge
                  v-if="!conversa.lida && !conversa.enviadoPorCliente"
                  color="primary"
                  dot
                  inline
                ></v-badge>
              </div>
            </v-list-item-subtitle>
          </v-list-item>
        </v-list>
      </div>
    </div>
  <ChatModalPrestador
      v-if="dialogChat && clienteIdSelecionado"
      :cliente-id="clienteIdSelecionado"
      :cliente-nome="clienteNomeSelecionado"
      @close="fecharChat"
    />
  </div>
</template>

<style scoped>
.conversa-item {
  border-radius: 8px;
  transition: all 0.2s ease;
}

.conversa-item:hover {
  background-color: rgba(25, 118, 210, 0.04);
  transform: translateX(2px);
}

.conversa-item:active {
  background-color: rgba(25, 118, 210, 0.08);
}

.has-unread {
  background-color: rgba(25, 118, 210, 0.05);
}

.has-unread:hover {
  background-color: rgba(25, 118, 210, 0.09);
}

.overflow-y-auto::-webkit-scrollbar {
  width: 4px;
}

.overflow-y-auto::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.overflow-y-auto::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.border-b {
  border-bottom: 1px solid rgba(0, 0, 0, 0.12);
}
</style>