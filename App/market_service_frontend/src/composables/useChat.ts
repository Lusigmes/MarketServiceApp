import { ref, computed, onUnmounted } from 'vue';
import { webSocketService } from '@/api/websocket/WebSocketService';
import { buscarHistoricoChat, enviarMensagem as enviarMensagemAPI } from '@/api/ChatService';
import type { ChatMessageInterface, ChatMessageResponseInterface } from '@/types';
import { useAuth } from './useAuth';
import { findClienteIdByUsuarioId } from '@/api/ClienteService';

export function useChat() {
    const { usuario } = useAuth();
    const messages = ref<ChatMessageResponseInterface[]>([]);
    const loading = ref(false);
    const error = ref<string | null>(null);
    const currentConversation = ref<{
        clienteId: number;
        prestadorId: number;
    } | null>(null);

    const initializeWebSocket = async () => {
        if (!usuario.value) {
            console.error('Usuário não autenticado');
            error.value = 'Usuário não autenticado';
            return;
        }

        try {
            let idParaConectar = usuario.value.id;
            
            if (usuario.value.tipoUsuario === 'CLIENTE') {
                try {
                    idParaConectar = await findClienteIdByUsuarioId(usuario.value.id);
                } catch (err) {
                    console.error('Erro ao obter clienteId:', err);
                }
            }
            
            await webSocketService.connect(idParaConectar);
            
            webSocketService.onMessage(handleIncomingMessage);
            webSocketService.onError(handleWebSocketError);
            
            
        } catch (err: any) {
            console.error('Erro ao inicializar WebSocket:', err);
            error.value = 'Não foi possível conectar ao chat. ' + (err.message || '');
        }
    };

    const handleIncomingMessage = (message: ChatMessageResponseInterface) => {      
        if(currentConversation.value &&
            message.clienteId === currentConversation.value.clienteId &&
            message.prestadorId === currentConversation.value.prestadorId
        ){

            const exists = messages.value.some(m => 
                    m.id === message.id || 
                    (m.conteudo === message.conteudo && m.dataEnvio === message.dataEnvio)
                );
                
                if (!exists) {
                    messages.value.push(message);
                    
                    messages.value.sort((a, b) => 
                        new Date(a.dataEnvio).getTime() - new Date(b.dataEnvio).getTime()
                    );
                }
        }
        
    };

    const handleWebSocketError = (errorMsg: string) => {
        console.error('Erro WebSocket:', errorMsg);
        error.value = errorMsg;
    };

    const loadConversation = async (clienteId: number, prestadorId: number) => {
        try {
            loading.value = true;
            error.value = null;
            
            const historico = await buscarHistoricoChat(clienteId, prestadorId);
            
            messages.value = historico;
            
            currentConversation.value = { clienteId, prestadorId };
            
            const status = webSocketService.getConnectionStatus();
            if (status.isConnected) {
                webSocketService.sendJoinMessage(clienteId, prestadorId);
            }
            
        } catch (err: any) {
            console.error('Erro ao carregar conversa:', err);
            error.value = 'Erro ao carregar conversa: ' + (err.message || '');
            messages.value = [];
            currentConversation.value = null;
        } finally {
            loading.value = false;
        }
    };
    const sendMessage = async (conteudo: string) => {
        if (!currentConversation.value || !conteudo.trim() || !usuario.value) {
            console.error('Não pode enviar - dados inválidos');
            throw new Error('Dados inválidos para enviar mensagem');
        }

        try {
            
            const isClient = usuario.value.tipoUsuario === 'CLIENTE';
            const message: ChatMessageInterface = {
                tipo: 'CHAT',
                clienteId: currentConversation.value.clienteId,
                prestadorId: currentConversation.value.prestadorId,
                conteudo: conteudo.trim(),
                enviadoPorCliente: isClient
            };

            
            const savedMessage = await enviarMensagemAPI(message);
            
            messages.value.push(savedMessage);
            
            messages.value.sort((a, b) => 
                new Date(a.dataEnvio).getTime() - new Date(b.dataEnvio).getTime()
            );
            
            const status = webSocketService.getConnectionStatus();
            if (status.isConnected) {
                console.log('WebSocket conectado + API');
                // webSocketService.send(message);
            } else {
                console.log('WebSocket não conectado, apenas API REST usada');
            }
            
            return savedMessage;
            
        } catch (err: any) {
            console.error('Erro ao enviar mensagem:', err);
            error.value = 'Erro ao enviar mensagem: ' + (err.message || '');
            throw err;
        }
    }; 
   
/* versão de envio wwebsocket(bug)
 const sendMessage = async (conteudo: string) => {
        if (!currentConversation.value || !conteudo.trim() || !usuario.value) {
            console.error('Não pode enviar - dados inválidos');
            throw new Error('Dados inválidos para enviar mensagem');
        }

        try {
            const isClient = usuario.value.tipoUsuario === 'CLIENTE';
            const message: ChatMessageInterface = {
                tipo: 'CHAT',
                clienteId: currentConversation.value.clienteId,
                prestadorId: currentConversation.value.prestadorId,
                conteudo: conteudo.trim(),
                enviadoPorCliente: isClient
            };

            const status = webSocketService.getConnectionStatus();
            if (!status.isConnected) {
                console.error('WebSocket não conectado. Não é possível enviar a mensagem.');
                throw new Error('WebSocket não conectado');
            }

            // Envia via WebSocket (servidor deve persistir e retransmitir para o outro usuário)
            webSocketService.send(message);

            // Exibe imediatamente a mensagem localmente (a mensagem definitiva deve vir via WebSocket do servidor)
            messages.value.push({
                id: Date.now(),
                tipo: 'CHAT',
                clienteId: message.clienteId,
                clienteNome: '',
                prestadorId: message.prestadorId,
                prestadorNome: '',
                conteudo: message.conteudo,
                enviadoPorCliente: message.enviadoPorCliente,
                lida: false,
                dataEnvio: new Date().toISOString()
            });

            messages.value.sort((a, b) =>
                new Date(a.dataEnvio).getTime() - new Date(b.dataEnvio).getTime()
            );

            return message;
        } catch (err: any) {
            console.error('Erro ao enviar mensagem:', err);
            error.value = 'Erro ao enviar mensagem: ' + (err.message || '');
            throw err;
        }
    };
*/
    const clearChat = () => {
        messages.value = [];
        currentConversation.value = null;
        error.value = null;
    };

    const getConnectionStatus = () => {
        return webSocketService.getConnectionStatus();
    };

    onUnmounted(() => {
        webSocketService.removeMessageHandler(handleIncomingMessage);
        clearChat();
    });

    return {
        messages: computed(() => messages.value),
        loading: computed(() => loading.value),
        error: computed(() => error.value),
        currentConversation: computed(() => currentConversation.value),
        initializeWebSocket,
        loadConversation,
        sendMessage,
        clearChat,
        getConnectionStatus
    };
}