import type { DemandaResponseInterface } from "@/types";
import httpConnect from "./connect/connect";

export const carregarDemandas = async () => {
    try {
        const { data } = await httpConnect.get("/demandas");
        return data;
    } catch (error) {
        console.error("Erro: ", error)
        throw error;
    }
};

export const criarDemanda = async ( demanda: Partial<DemandaResponseInterface>) => {
    const { data } = await httpConnect.post("/demandas", demanda);
    return data;
}

export const atualizarDemanda = async ( id: number, demanda: Partial<DemandaResponseInterface>, clienteId: number | null) => {
    const { data } = await httpConnect.put(`/demandas/${id}?clienteId=${clienteId}`, demanda);
    return data;
}

export const deletarDemanda = async (id:number) => {
    return await httpConnect.delete(`/demandas/${id}`); 
}


