import httpConnect from "./connect/connect";

export const carregarPrestadores = async () => {
    try {
        const { data } = await httpConnect.get("/prestadores");
        return data;
    } catch (error) {
        console.error("Erro: ", error)
        throw error;
    }
};