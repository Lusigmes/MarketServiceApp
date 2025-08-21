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