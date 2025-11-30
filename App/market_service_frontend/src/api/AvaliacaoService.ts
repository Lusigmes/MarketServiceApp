import httpConnect from "./connect/connect";
import type { AvaliacaoResponseInterface, AvaliacaoInterface } from '@/types';

export const carregarAvaliacoesPorPrestadorPaginado = async (
    prestadorId: number, page: number, size: number, sort?: string
): Promise<{ content: AvaliacaoResponseInterface[]; totalPages: number; totalElements: number }> => {
    try {
        const { data } = await httpConnect.get(`/avaliacoes/prestador/${prestadorId}/paginadas`, {
            params: {
                page,
                size,
                sort
            }
        });
        return data;
    } catch (error) {
        throw error;
    }
};
export const carregarAvaliacoesPorClientePaginado = async (
    clienterId: number, page: number, size: number, sort?: string
): Promise<{ content: AvaliacaoResponseInterface[]; totalPages: number; totalElements: number }> => {
    try {
        const { data } = await httpConnect.get(`/avaliacoes/cliente/${clienterId}/paginadas`, {
            params: {
                page,
                size,
                sort
            }
        });
        return data;
    } catch (error) {
        throw error;
    }
};

export const carregarEstatisticasPrestador = async (prestadorId: number): Promise<{ media: number; totalAvaliacoes: number }> => {
    try {
        const { data } = await httpConnect.get(`/avaliacoes/prestador/${prestadorId}/estatisticas`);
        return data;
    } catch (error) {
        throw error;
    }
};

export const criarAvaliacao = async (avaliacao: AvaliacaoInterface): Promise<AvaliacaoResponseInterface> => {
    try {
        const { data } = await httpConnect.post("/avaliacoes", avaliacao);
        return data;
    } catch (error) {
        throw error;
    }
};

export const deletarAvaliacao = async (avaliacaoId: number): Promise<void> => {
    try {
        await httpConnect.delete(`/avaliacoes/${avaliacaoId}`);
    } catch (error) {
        throw error;
    }
};

export const carregarAvaliacoes = async (): Promise<AvaliacaoResponseInterface[]> => {
    try {
        const { data } = await httpConnect.get("/avaliacoes");
        return data;
    } catch (error) {
        throw error;
    }
};

export const verificarAvaliacaoExistente = async (
    clienteId: number, 
    prestadorId: number, 
    demandaId: number
): Promise<{ existeAvaliacao: boolean }> => {
    const response = await httpConnect.get('/avaliacoes/verificar', {
        params: { clienteId, prestadorId, demandaId }
    });
    return response.data;
};