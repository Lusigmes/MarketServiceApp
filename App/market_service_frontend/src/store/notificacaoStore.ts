import httpConnect from "@/api/connect/connect";
import type { Notificacao } from "@/types";
import { defineStore } from "pinia";
import { ref } from "vue";

export const useNotificacaoStore =  defineStore("notificacao", () => {
    const notificacoes = ref<Notificacao[]>([]);

    async function carregar(usuarioId: number) {
    try {
        const { data } = await httpConnect.get(`/notificacoes/${usuarioId}`);
        notificacoes.value = data;
    } catch (error) {
        console.error("Erro ao carregar notificações:", error);
        notificacoes.value = []; // evita travar o componente
    }
}

async function marcarComoLida(id: number){
    try {
        await httpConnect.post(`/notificacoes/${id}/lida`);
        const n = notificacoes.value.find((n) => n.id === id);
        if (n) n.lida = true;
    } catch (error) {
            console.error("Erro ao marcar como Lida:", error); 
    }
 
    };
    return {
        notificacoes,
        carregar,
        marcarComoLida
    };
});