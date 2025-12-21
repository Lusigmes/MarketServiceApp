import httpConnect from "./connect/connect";

// export const findClienteIdByUsuarioId = async(usuarioId: number): Promise<number> => {
//     const {data} = await httpConnect.get<number>(`/clientes/usuario/${usuarioId}`);
//     return data;
// }

export const findClienteIdByUsuarioId = async (usuarioId: number): Promise<number> => {
    const { data } = await httpConnect.get<number>(`/clientes/usuario/${usuarioId}`);
    // Se retornar null/undefined, lance um erro
    if (data === null || data === undefined) {
        throw new Error(`Cliente não encontrado para usuário ID: ${usuarioId}`);
    }
    return data;
};