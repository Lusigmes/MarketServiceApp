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

export const carregarPrestadoresPaginado = async (page:0, size: 5, sort:"usuario.nome, asc") => {
    try {
        const { data } = await httpConnect.get("/prestadores/paginados",{
            params:{
                page,
                size:5,
                sort
            }
        });
        return data;
    } catch (error) {
        throw error;
    }
}