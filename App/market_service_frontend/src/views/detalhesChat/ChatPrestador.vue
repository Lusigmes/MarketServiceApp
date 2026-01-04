<script setup lang="ts">
import { findPrestadorIdByUsuarioId } from '@/api/PrestadorService';
import { useAuth } from '@/composables/useAuth';
import { useChat } from '@/composables/useChat';
import { useNotification } from '@/composables/useNotification';
import { formatarDataParaExibicao } from '@/utils/dateUtils';
import { computed, nextTick, onMounted, onUnmounted, ref, watch } from 'vue';

interface Props {
  clienteId: number;
  podeIniciar?: boolean; 
  clienteNome?: string | null;

}

interface Emits {
  (e: 'close') : void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const {usuario} = useAuth();
const {
  messages, loading, error, initializeWebSocket,
  loadConversation, sendMessage, clearChat } = useChat();

const novaMensagem = ref('');
const enviando = ref(false);
const containerMensagens = ref<HTMLElement | null>(null);
const prestadorId = ref<number|null>(null);
const {showNotification} = useNotification();
const clienteNome = ref<string>(props.clienteNome ?? 'Cliente');

watch(() => props.clienteNome, (val) => {
  clienteNome.value = val ?? 'Cliente';
});

onMounted(async () => {
  if (!usuario.value) return;
  try {
    prestadorId.value = await findPrestadorIdByUsuarioId(usuario.value.id);
    
    await initializeWebSocket();
    
    if (prestadorId.value) {
      await loadConversation(props.clienteId, prestadorId.value);
      
      if (messages.value.length > 0) {
        const primeiraMsg = messages.value[0];
        if (primeiraMsg.enviadoPorCliente && primeiraMsg.clienteNome) {
          clienteNome.value = primeiraMsg.clienteNome;
        }
      }
      
      scrollToBottom();
    }
  } catch (err) {
    console.error('Erro ao carregar chat:', err);
  }
});

const scrollToBottom = () => {
  nextTick(() => {
    if (containerMensagens.value) {
      containerMensagens.value.scrollTop = containerMensagens.value.scrollHeight;
    }
  });
};

watch(messages, () => {
  scrollToBottom();
}, { deep: true });

onUnmounted(() => {
  clearChat();
});

const quebrarTexto = (texto: string, limite = 100) => {
  if (!texto) return '';
  if (texto.length <= limite) return texto;
  return texto.match(new RegExp(`.{1,${limite}}`, 'g'))?.join('\n') || texto;
};

const podeEnviarPrimeiraMensagem = computed(() => {
  return props.podeIniciar === true;
});

const podeEnviar = computed(() => {
  return podeEnviarPrimeiraMensagem.value || messages.value.some(msg => msg.enviadoPorCliente);
});

const inputHabilitado = computed(() => {
  return podeEnviar.value && !enviando.value && !loading.value;
});

const enviarMensagem = async () => {
  const texto = novaMensagem.value.trim();
  
  if (!texto || !prestadorId.value || enviando.value) return;

  enviando.value = true;
  try {
    await sendMessage(texto);
    novaMensagem.value = '';
  } catch (err: any) {
    console.error('Erro detalhado:', {
      erro: err,
      resposta: err.response?.data,
      status: err.response?.status
    });
    
    const errorData = err.response?.data;
    
    if (errorData && typeof errorData === 'string' && errorData.includes('Prestadores não podem iniciar')) {
      showNotification("Aguarde o cliente iniciar a conversa antes de responder.", "warning");
    } 
    else if (err.response?.status === 500) {
      const errorMessage = errorData?.message || errorData?.error || 'Erro interno no servidor';
      showNotification(`Erro: ${errorMessage}`, "error");
      console.error('Erro 500 detalhado:', errorData);
    }
    else {
      showNotification("Erro ao enviar mensagem. Tente novamente.", "error");
    }
  } finally {
    enviando.value = false;
  }
};

const handleKeyDown = (event: KeyboardEvent) => {
  if (event.key === 'Enter' && !event.shiftKey && inputHabilitado.value) {
    event.preventDefault();
    enviarMensagem();
  }
};
</script>

<template>
  <div class="chat-prestador">
    <div class="chat-header">
      <div class="user-info">
        <v-avatar color="primary" size="40">
          <span class="text-white">
            {{ clienteNome.charAt(0).toUpperCase() }}
          </span>
        </v-avatar>
        <div class="user-details">
          <h3>{{ clienteNome }}</h3>
          <p class="text-caption text-medium-emphasis mb-0">Cliente</p>
        </div>
      </div>
      

    </div>

    <!-- msgs -->
    <div class="messages-container" ref="containerMensagens">
      <div v-if="loading" class="loading">
        <v-progress-circular indeterminate color="primary" />
        <span>Carregando conversa...</span>
      </div>

      <div v-else-if="error" class="error-message">
        <v-alert type="error" density="compact">
          {{ error }}
        </v-alert>
      </div>

      <div v-else-if="messages.length === 0 && podeIniciar" class="empty-chat can-initiate">
        <v-icon size="64" color="grey">mdi-chat-outline</v-icon>
        <p class="font-weight-medium">Você pode iniciar a conversa</p>
        <p class="text-caption text-medium-emphasis text-center px-4">
          Como sua proposta foi aceita, você pode começar a conversa com o cliente.
        </p>
      </div>

      <div v-else-if="messages.length === 0" class="empty-chat">
        <v-icon size="64" color="green">mdi-chat-outline</v-icon>
        <p>Nenhuma mensagem ainda</p>
        <p class="text-caption text-medium-emphasis">
          Aguarde o cliente iniciar esta conversa.
        </p>
      </div>

      <!-- msgs -->
      <div v-else class="messages-list">
        <div
          v-for="message in messages"
          :key="message.id"
          :class="[
            'message',
            message.enviadoPorCliente ? 'received' : 'sent',
            message.tipo === 'SYSTEM' ? 'system' : '',
            message.tipo === 'ERROR' ? 'error' : ''
          ]"
        >
          <!-- msg de sistema -->
          <div v-if="message.tipo === 'SYSTEM' || message.tipo === 'ERROR'" class="system-message">
            <v-icon :color="message.tipo === 'ERROR' ? 'error' : 'info'" size="small">
              {{ message.tipo === 'ERROR' ? 'mdi-alert' : 'mdi-information' }}
            </v-icon>
            <span>{{ message.conteudo }}</span>
          </div>

          <!-- msg normal -->
          <div v-else class="message-content">
            <div class="message-bubble">
              <div class="message-header">
                <strong>{{ message.enviadoPorCliente ? clienteNome : 'Você' }}</strong>
              </div>
              <p>{{ quebrarTexto(message.conteudo) }}</p>
              <div class="message-meta">
                <span class="time">{{ formatarDataParaExibicao(message.dataEnvio) }}</span>
                <v-icon
                  v-if="!message.enviadoPorCliente"
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
        :placeholder="podeEnviar ? 'Digite sua mensagem...' : 'Aguarde o cliente iniciar a conversa...'"
        variant="outlined"
        density="compact"
        rows="1"
        auto-grow
        hide-details
        :disabled="!inputHabilitado"
        @keydown="handleKeyDown"
      />
      <v-btn
        color="primary"
        :loading="enviando"
        :disabled="!novaMensagem.trim() || !inputHabilitado"
        @click="enviarMensagem"
        icon
      >
        <v-icon>mdi-send</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<style scoped>
.chat-prestador {
  display: flex;
  flex-direction: column;
  height: 500px;
  max-height: 80vh;
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
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.sent {
  justify-content: flex-end;
}

.message.received {
  justify-content: flex-start;
}

.system-message {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #e3f2fd;
  border-radius: 16px;
  color: #1976d2;
  font-size: 14px;
  margin: 4px auto;
  max-width: 80%;
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
  font-size: 11px;
  opacity: 0.8;
  gap: 4px;
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

.permissao-badge {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.7; }
  100% { opacity: 1; }
}

.empty-chat.can-initiate {
  border: 2px dashed rgba(76, 175, 80, 0.3);
  border-radius: 12px;
  padding: 20px;
  margin: 16px;
  background: rgba(76, 175, 80, 0.05);
}

.empty-chat.can-initiate p:first-of-type {
  color: #2e7d32;
  font-weight: 600;
}
</style>