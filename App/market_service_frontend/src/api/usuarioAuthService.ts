import httpConnect from "@/api/connect/connect";
import type {UsuarioResponseInterface, RegistroUsuarioInterface, LoginUsuarioInterface, LoginTokenResponseInterface } from "@/types/usuario";


export const registrarUsuario = async (data: RegistroUsuarioInterface) => {
    const response = await httpConnect.post<UsuarioResponseInterface>("/auth/registro", data);
    return response.data;
}

export const logarUsuario = async (data: LoginUsuarioInterface) => {
    const response = await httpConnect.post<LoginTokenResponseInterface>("/auth/login", data);
    return response.data;
}

export const getUsuarioAutenticado = async () => {
    const response = await httpConnect.get<UsuarioResponseInterface>("/auth/me");
    return response.data;
}

export const listarUsuarios = async () => {
    const response = await httpConnect.get<UsuarioResponseInterface>("/usuarios");
    return response.data;
}

export const deletarUsuario = async (id: number) => {
    await httpConnect.delete(`/usuarios/${id}`);
}

