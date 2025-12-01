import type { UsuarioResponseInterface } from "@/types";
import httpConnect from "./connect/connect";

export const atualizarUsuario = async (id: number, dados:Partial<UsuarioResponseInterface>): Promise<UsuarioResponseInterface> =>{
    const {data} = await httpConnect.put(`/usuarios/${id}`, dados);
    return data;
};


export const listarUsuarios = async () => {
    const response = await httpConnect.get<UsuarioResponseInterface>("/usuarios");
    return response.data;
}

export const deletarUsuario = async (id: number) => {
    await httpConnect.delete(`/usuarios/${id}`);
}

