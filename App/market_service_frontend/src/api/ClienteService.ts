import httpConnect from "./connect/connect";

export const findClienteIdByUsuarioId = async(usuarioId: number): Promise<number> => {
    const {data} = await httpConnect.get<number>(`/clientes/usuario/${usuarioId}`);
    return data;
}

