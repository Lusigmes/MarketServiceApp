import { ref } from 'vue';
import type { ChatMessageInterface, ChatMessageResponseInterface } from '@/types';

export class WebSocketService {
    private socket: WebSocket | null = null;
    private isConnecting = ref(false);
    private isConnected = ref(false);
    private messageHandlers: Array<(message: ChatMessageResponseInterface) => void> = [];
    private errorHandlers: Array<(error: string) => void> = [];

    public connect(userId: number): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.socket?.readyState === WebSocket.OPEN) {
                this.isConnected.value = true;
                resolve();
                return;
            }

            if (this.isConnecting.value) {
                return;
            }

            this.isConnecting.value = true;
            
            const wsUrl = `ws://localhost:8080/ws/chat?userId=${userId}`;
            console.log('Conectando ao WebSocket:', wsUrl);
            
            this.socket = new WebSocket(wsUrl);

            this.socket.onopen = () => {
                this.isConnected.value = true;
                this.isConnecting.value = false;
                resolve();
            };

            this.socket.onmessage = (event) => {
                try {
                    const message = JSON.parse(event.data);
                    this.notifyMessageHandlers(message);
                } catch (error) {
                    console.error('Erro ao processar mensagem:', error);
                }
            };

            this.socket.onerror = (error) => {
                console.error('Erro na conexão WebSocket');
                this.isConnecting.value = false;
                this.notifyErrorHandlers('Erro na conexão WebSocket');
                reject(new Error('Erro na conexão WebSocket'));
            };

            this.socket.onclose = (event) => {
                this.isConnected.value = false;
                this.isConnecting.value = false;
                this.socket = null;
                
                if (event.code !== 1000) {
                    console.warn('Tentando reconectar.');
                    setTimeout(() => {
                        if (!this.isConnected.value && !this.isConnecting.value) {
                            this.connect(userId);
                        }
                    }, 3000);
                }
            };
        });
    }

    public disconnect(): void {
        if (this.socket) {
            this.socket.close(1000, 'Desconexão normal pelo usuário');
            this.socket = null;
        }
        this.isConnected.value = false;
        this.isConnecting.value = false;
    }

    public send(message: ChatMessageInterface): void {
        if (this.socket?.readyState === WebSocket.OPEN) {
            this.socket.send(JSON.stringify(message));
        } else {
            console.error('WebSocket não está conectado. Estado:', this.socket?.readyState);
            this.notifyErrorHandlers('WebSocket não conectado');
            
            if (!this.isConnecting.value) {
                console.warn('Tentando reconectar');
            }
        }
    }

    public sendJoinMessage(clienteId: number, prestadorId: number): void {
        const joinMessage: ChatMessageInterface = {
            tipo: 'JOIN',
            clienteId,
            prestadorId,
            conteudo: '',
            enviadoPorCliente: true
        };
        this.send(joinMessage);
    }

    public onMessage(handler: (message: ChatMessageResponseInterface) => void): void {
        this.messageHandlers.push(handler);
    }

    public onError(handler: (error: string) => void): void {
        this.errorHandlers.push(handler);
    }

    public removeMessageHandler(handler: (message: ChatMessageResponseInterface) => void): void {
        const index = this.messageHandlers.indexOf(handler);
        if (index > -1) {
            this.messageHandlers.splice(index, 1);
        }
    }

    private notifyMessageHandlers(message: ChatMessageResponseInterface): void {
        this.messageHandlers.forEach(handler => handler(message));
    }

    private notifyErrorHandlers(error: string): void {
        this.errorHandlers.forEach(handler => handler(error));
    }

    public getConnectionStatus() {
        return {
            isConnected: this.isConnected.value,
            isConnecting: this.isConnecting.value,
            readyState: this.socket?.readyState
        };
    }
}

export const webSocketService = new WebSocketService();