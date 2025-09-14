import type { DemandaResponseInterface } from "@/types";
import httpConnect from "./connect/connect";

export const carregarDemandas = async (page = 0, size = 8, sort = 'dataCriacaoDemanda,DESC') => {
    try {
        const { data } = await httpConnect.get("/demandas",{
            params:{
                page,
                size:9,
                sort
            }
        });
        return data;
    } catch (error) {
        throw error;
    }
};

export const criarDemanda = async ( demanda: Partial<DemandaResponseInterface>) => {
    const { data } = await httpConnect.post("/demandas", demanda);
    return data;
}

export const atualizarDemanda = async ( idDemanda: number, demanda: Partial<DemandaResponseInterface>, clienteId: number | null) => {
    const { data } = await httpConnect.put(`/demandas/${idDemanda}?clienteId=${clienteId}`, demanda);
    return data;
}

export const getDemandaById = async (id:number) => {
    return await httpConnect.get(`/demandas/${id}`); 
}

export const deletarDemanda = async (id:number) => {
    return await httpConnect.delete(`/demandas/${id}`); 
}


