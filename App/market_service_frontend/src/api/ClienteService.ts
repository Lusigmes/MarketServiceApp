import httpConnect from "./connect/connect";

export const findClienteIdByUsuarioId = async (usuarioId: number): Promise<number> => {
    const { data } = await httpConnect.get<number>(`/clientes/usuario/${usuarioId}`);
    if (data === null || data === undefined) {
        throw new Error(`Cliente não encontrado para usuário ID: ${usuarioId}`);
    }
    return data;
};

export const findNomeClienteByClienteId = async (
  clienteId: number
): Promise<string> => {
  const { data } = await httpConnect.get<string>(
    `/clientes/nomeCliente/${clienteId}`
  );
  if (!data) {    throw new Error(`Nome do cliente não encontrado para cliente ID: ${clienteId}`);  }
  return data;
};