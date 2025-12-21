import type { 
    ChatMessageInterface, 
    ChatMessageResponseInterface,
    ChatConversationInterface 
} from "@/types";
import httpConnect from "./connect/connect";

export const enviarMensagem = async (mensagem: ChatMessageInterface): Promise<ChatMessageResponseInterface> => {
    try {
        const { data } = await httpConnect.post<ChatMessageResponseInterface>("/chats", mensagem);
        return data;
    } catch (error) {
        throw error;
    }
};

export const buscarHistoricoChat = async (
    clienteId: number, 
    prestadorId: number
): Promise<ChatMessageResponseInterface[]> => {
    try {
        const { data } = await httpConnect.get<ChatMessageResponseInterface[]>("/chats/historico", {
            params: { clienteId, prestadorId }
        });
        return data;
    } catch (error) {
        throw error;
    }
};

export const carregarMensagemPorId = async (msgId:number): Promise<ChatMessageResponseInterface> => {
    try {
        const {data} = await httpConnect.get(`/chats/mensagem/${msgId}`);
        return data;
    } catch (error) {
        console.error('Erro ao carregar mensagem:', error);
        throw error   
    }
};

export const carregarMenssagens = async (): Promise<ChatMessageResponseInterface[]> => {
    try {
        const {data} = await httpConnect.get(`/chats`);
        return data;
    } catch (error) {
        console.error('Erro ao carregar mensagens:', error);
        throw error   
    }
};

export const buscarMensagensNaoLidas = async (
    clienteId: number, 
    prestadorId: number, 
    enviadoPorCliente: boolean
): Promise<ChatMessageResponseInterface[]> => {
    try {
        const { data } = await httpConnect.get<ChatMessageResponseInterface[]>("/chats/nao-lidas", {
            params: { clienteId, prestadorId, enviadoPorCliente }
        });
        return data;
    } catch (error) {
        throw error;
    }
};

export const marcarMensagemComoLida = async (msgId: number): Promise<ChatMessageResponseInterface> => {
    try {
        const { data } = await httpConnect.post<ChatMessageResponseInterface>(`/chats/${msgId}/ler`);
        return data;
    } catch (error) {
        throw error;
    }
};

export const marcarMensagensComoLidas = async (messageIds: number[]): Promise<void> => {
    try {
        await httpConnect.post("/chats/marcar-lidas", messageIds);
    } catch (error) {
        throw error;
    }
};

// Novas funções para conversas
export const buscarConversasCliente = async (clienteId: number): Promise<ChatConversationInterface[]> => {
    try {
        const { data } = await httpConnect.get<ChatConversationInterface[]>(`/chats/conversas/cliente/${clienteId}`);
        return data;
    } catch (error) {
        throw error;
    }
};

export const buscarConversasPrestador = async (prestadorId: number): Promise<ChatConversationInterface[]> => {
    try {
        const { data } = await httpConnect.get<ChatConversationInterface[]>(`/chats/conversas/prestador/${prestadorId}`);
        return data;
    } catch (error) {
        throw error;
    }
};

export const buscarMensagensPorCliente = async (clienteId: number): Promise<ChatMessageResponseInterface[]> => {
    try {
        const { data } = await httpConnect.get<ChatMessageResponseInterface[]>(`/chats/cliente/${clienteId}`);
        return data;
    } catch (error) {
        throw error;
    }
};

export const buscarMensagensPorPrestador = async (prestadorId: number): Promise<ChatMessageResponseInterface[]> => {
    try {
        const { data } = await httpConnect.get<ChatMessageResponseInterface[]>(`/chats/prestador/${prestadorId}`);
        return data;
    } catch (error) {
        throw error;
    }
};