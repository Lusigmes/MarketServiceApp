import type { ChatMessageResponseInterface } from "@/types";
import httpConnect from "./connect/connect";



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