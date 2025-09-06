import type { PropostaResponseInterface } from "@/types";
import httpConnect from "./connect/connect";

export const carregarPropostas = async (demandaId:number, page = 0, size = 5, sort = 'dataCriacaoProposta,DESC') => {
    try {
        const { data } = await httpConnect.get("/propostas",{
            params:{
                demandaId,
                page,
                size:5,
                sort
            }
        });
        return data;
    } catch (error) {
        console.error("Erro: ", error)
        throw error;
    }
};

export const carregarPropostasDoPrestador = async (prestadorId:number, page = 0, size = 10, sort = 'dataCriacaoProposta,DESC') => {
    try {
        const { data } = await httpConnect.get("/propostas/doPrestador",{
            params:{
                prestadorId,
                page,
                size,
                sort
            }
        });
        console.log("carregarPropostasDoPrestador: ", data)
        return data;
    } catch (error) {
        console.error("Erro: ", error)
        throw error;
    }
};

export const criarProposta= async ( proposta: Partial<PropostaResponseInterface>) => {
    const { data } = await httpConnect.post("/propostas", proposta);
    return data;
}

export const atualizarProposta = async ( idProposta: number, proposta: Partial<PropostaResponseInterface>) => {
    const { data } = await httpConnect.put(`/propostas/${idProposta}`, proposta);
    return data;
}

// export const deletarDemanda = async (id:number) => {
//     return await httpConnect.delete(`/demandas/${id}`); 
// }


