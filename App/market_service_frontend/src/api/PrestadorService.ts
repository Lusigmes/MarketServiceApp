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



export const findPrestadorIdByUsuarioId = async(usuarioId: number): Promise<number> => {
    const {data} = await httpConnect.get<number>(`/prestadores/usuario/${usuarioId}`);
    return data;
};