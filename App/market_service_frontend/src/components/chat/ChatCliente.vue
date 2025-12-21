<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import { useChat } from '@/composables/useChat';
import { useAuth } from '@/composables/useAuth';
import type { PrestadorResponseInterface } from '@/types';
import { findClienteIdByUsuarioId } from '@/api/ClienteService';
import { formatarDataParaExibicao } from '@/utils/dateUtils';

interface Props {
  prestador: PrestadorResponseInterface;
}

const props = defineProps<Props>();
const { usuario } = useAuth();
const {
  messages,
  loading,
  error,
  initializeWebSocket,
  loadConversation,
  sendMessage,
  getConnectionStatus,
  clearChat
} = useChat();

const novaMensagem = ref('');
const enviando = ref(false);
const containerMensagens = ref<HTMLElement | null>(null);

const podeEnviar = ref(true);

onMounted(async () => {
  //carrega oa id do cliente, inicializa o WS, e carrega a conversa

  if (!usuario.value) {
    console.error("❌ Usuário não autenticado");
    return;
  }
  
  try {
    let clienteId = 0;
    const prestadorId = props.prestador.id;
    
    if (usuario.value.tipoUsuario === 'CLIENTE') {
      clienteId = await findClienteIdByUsuarioId(usuario.value.id);
    } else {
      return;
    }
    
    await initializeWebSocket();
    
    await loadConversation(clienteId, prestadorId);
    
    scrollToBottom();
    
  } catch (err) {
    console.error("❌ Erro ao iniciar chat:", err);
  }
});

watch(messages, () => {
  nextTick(() => scrollToBottom());
}, { deep: true });

onUnmounted(() => {
  clearChat();
});

const scrollToBottom = () => {
  if (containerMensagens.value) {
    containerMensagens.value.scrollTop = containerMensagens.value.scrollHeight;
  }
};

const sendMessageAction = async () => {
  const texto = novaMensagem.value.trim();
  if (!texto || enviando.value) return;
  
  
  enviando.value = true;
  try {
    await sendMessage(texto);
    novaMensagem.value = '';
  } catch (err: any) {
    console.error("❌ Erro ao enviar mensagem:", err);
    if (err.response?.status === 403) {
      alert("Você não tem permissão para enviar mensagens nesta conversa");
    } else {
      alert("Erro ao enviar mensagem. Tente novamente.");
    }
  } finally {
    enviando.value = false;
  }
};

const quebrarTexto = (texto: string, limite = 100) => {
  if (!texto) return '';
  if (texto.length <= limite) return texto;
  
  return texto.match(new RegExp(`.{1,${limite}}`, 'g'))?.join('\n') ?? texto;
};

//  shift+enter para nova linha e enter para enviar
const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    sendMessageAction();
  }
};
</script>

<template>
  <div class="chat-simple">
    <div class="chat-header">
      <div class="user-info">
        <v-avatar color="primary" size="40">
          <span class="text-white">
            {{ prestador.usuario.nome.charAt(0).toUpperCase() }}
          </span>
        </v-avatar>
        <div class="user-details">
          <h3>{{ prestador.usuario.nome }}</h3>
          <span class="specialization">{{ prestador.especializacao }}</span>
        </div>
      </div>
      <div class="status">
      </div>
    </div>

    <div class="messages-container" ref="containerMensagens">
      <div v-if="loading" class="loading">
        <v-progress-circular indeterminate color="primary" />
        <span>Carregando mensagens...</span>
      </div>


      <div v-else-if="error" class="error-message">
        <v-alert type="warning" density="compact">
          {{ error }}
          <br><small>Você ainda pode enviar mensagens.</small>
        </v-alert>
      </div>

 
      <div v-else-if="messages.length === 0" class="empty-chat">
        <v-icon size="64" color="grey">mdi-chat-outline</v-icon>
        <p>Nenhuma mensagem ainda. Inicie a conversa!</p>
      </div>

      <!-- msgs -->
      <div v-else class="messages-list">
        <div
          v-for="message in messages"
          :key="message.id"
          :class="[
            'message',
            message.enviadoPorCliente ? 'sent' : 'received',
            message.tipo === 'SYSTEM' ? 'system' : '',
            message.tipo === 'ERROR' ? 'error' : ''
          ]"
        >
          <!-- msg de sistema -->
          <div v-if="message.tipo === 'SYSTEM' || message.tipo === 'ERROR'" class="system-message">
            <v-icon
              :color="message.tipo === 'ERROR' ? 'error' : 'info'"
              size="small"
              class="mr-1"
            >
              {{ message.tipo === 'ERROR' ? 'mdi-alert' : 'mdi-information' }}
            </v-icon>
            <span>{{ message.conteudo }}</span>
          </div>

          <!-- msg normal -->
          <div v-else class="message-content">
            <div class="message-bubble">
              <div class="message-header">
                <strong>{{ message.enviadoPorCliente ? 'Você' : message.prestadorNome }}</strong>
              </div>
              <p>{{ quebrarTexto(message.conteudo, 100) }}</p>
              <div class="message-meta">
                <span class="time">{{ formatarDataParaExibicao(message.dataEnvio) }}</span>
                <v-icon
                  v-if="message.enviadoPorCliente"
                  :color="message.lida ? 'primary' : 'grey'"
                  size="x-small"
                  class="ml-1"
                >
                  {{ message.lida ? 'mdi-check-all' : 'mdi-check' }}
                </v-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="input-area">
      <v-textarea
        v-model="novaMensagem"
        placeholder="Digite sua mensagem..."
        variant="outlined"
        density="compact"
        rows="1"
        auto-grow
        hide-details
        :disabled="enviando || loading"
        @keydown="handleKeyDown"
        :loading="enviando"
      />
      <v-btn
        color="primary"
        :loading="enviando"
        :disabled="!novaMensagem.trim() || enviando || loading"
        @click="sendMessageAction"
        icon
      >
        <v-icon>mdi-send</v-icon>
      </v-btn>
    </div>
  </div>
</template>
<style scoped>
.chat-simple {
  display: flex;
  flex-direction: column;
  height: 500px;
  max-height: 80vh;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: white;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-details h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.specialization {
  font-size: 12px;
  color: #666;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #fafafa;
}

.loading, .empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #666;
  gap: 12px;
}

.error-message {
  padding: 16px;
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.message {
  display: flex;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.sent {
  justify-content: flex-end;
}

.message.received {
  justify-content: flex-start;
}

.message.system, .message.error {
  justify-content: center;
}

.system-message {
  background: #e3f2fd;
  border-radius: 16px;
  padding: 8px 12px;
  margin: 4px 0;
  font-size: 14px;
  color: #1976d2;
}

.message.error .system-message {
  background: #ffebee;
  color: #d32f2f;
}

.message-bubble {
  max-width: 100%;
  padding: 12px 16px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.5;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  position: relative;
  animation: popIn 0.2s ease-out;
}
.message-header {
  margin-bottom: 4px;
  font-size: 12px;
  opacity: 0.8;
}

@keyframes popIn {
  from {
    transform: scale(0.96);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

.sent .message-bubble {
  background: linear-gradient(135deg, #1976d2, #1565c0);
  color: white;
  border-bottom-right-radius: 6px;
}


.received .message-bubble {
  background: #ffffff;
  color: #333;
  border: 1px solid #e0e0e0;
  border-bottom-left-radius: 6px;
}


.message-bubble p {
  margin: 0 0 4px 0;
  font-size: 14px;
  line-height: 1.4;
}

.message-meta {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  margin-top: 4px;
  opacity: 0.75;
}

.sent .message-meta {
  color: rgba(255, 255, 255, 0.8);
}

.received .message-meta {
  color: #666;
}

.input-area {
  display: flex;
  gap: 8px;
  padding: 12px 16px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.input-area :deep(.v-field) {
  border-radius: 24px;
}

.messages-container::-webkit-scrollbar {
  width: 6px;
}

.messages-container::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.messages-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.messages-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>