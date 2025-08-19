import { ref } from "vue";
import { logarUsuario, registrarUsuario, getUsuarioAutenticado } from "@/api/usuarioAuthService";
import type {UsuarioResponseInterface, RegistroUsuarioInterface, LoginUsuarioInterface, LoginTokenResponseInterface } from "@/types/usuario";


const token = ref<string | null>(localStorage.getItem("jwt"));
const usuario = ref<UsuarioResponseInterface | null>(null);
const error = ref<string | null>(null);

export function useAuth(){
    
    const login = async (dadosUsuario: LoginUsuarioInterface) => {
        try {
            const response = await logarUsuario(dadosUsuario);
            token.value = response.token;
            localStorage.setItem("jwt", response.token);
            await fetchUsuario();
            return true;
        } catch (err: any) {
            error.value = err.response?.data?.message || "Falha no login";
            return false;
        }
    }
    const logout = () => {
        token.value = null;
        usuario.value = null;
        localStorage.removeItem("jwt");
    }

    const fetchUsuario = async () => {
            try {
                usuario.value = await getUsuarioAutenticado();
        } catch {
            usuario.value = null;
        }
    };

    const registro = async (dadosUsuario: RegistroUsuarioInterface) => {
        try {
            await registrarUsuario(dadosUsuario);
            await login({email:dadosUsuario.email, senha:dadosUsuario.senha});
            return true;
        } catch (err: any) {
            error.value = err.response?.data?.message || "Falha no registro";
            return false;
        }
    }

    return { token, usuario, error,
        login, registro, logout, fetchUsuario};
};