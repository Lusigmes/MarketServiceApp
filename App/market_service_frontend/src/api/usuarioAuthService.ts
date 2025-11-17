import httpConnect from "@/api/connect/connect";
import type {UsuarioResponseInterface, RegistroUsuarioInterface, LoginUsuarioInterface, LoginTokenResponseInterface } from "@/types";



export const logarUsuario = async (dados: LoginUsuarioInterface): Promise<LoginTokenResponseInterface> => {
    const response = await httpConnect.post('/auth/login', dados);
    return response.data;
};

export const registrarUsuario = async (dados: RegistroUsuarioInterface): Promise<void> => {
    await httpConnect.post('/auth/registro', dados);
};

export const getUsuarioAutenticado = async (): Promise<UsuarioResponseInterface> => {
    const response = await httpConnect.get('/auth/me');
    return response.data;
};
