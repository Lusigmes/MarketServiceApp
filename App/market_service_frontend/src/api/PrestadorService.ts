import type { PrestadorResponseInterface } from "@/types";
import httpConnect from "./connect/connect";

export const carregarPrestadores = async () => {
    try {
        const { data } = await httpConnect.get("/prestadores");
        return data;
    } catch (error) {
        throw error;
    }
};

export const findPrestadorIdByUsuarioId = async(usuarioId: number): Promise<number> => {
    const {data} = await httpConnect.get<number>(`/prestadores/usuario/${usuarioId}`);
    return data;
};

export const carregarPrestadoresPaginado = async (page: number, size: number, sort?: string) => {
    try {
        const { data } = await httpConnect.get("/prestadores/paginados", {
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
}

export const carregarPrestadorPorId = async (prestadorId: number): Promise<PrestadorResponseInterface> => {
    try {
        const { data } = await httpConnect.get(`/prestadores/${prestadorId}`);
        return data;
    } catch (error) {
        throw error;
    }
};