export interface RegistroUsuarioInterface{
    nome: string;
    cpf: string;
    email: string;
    senha: string;
    cep: string;
    tipoUsuario: "CLIENTE" | "PRESTADOR";

}

export interface LoginUsuarioInterface{
    email: string;
    senha: string;
}

export interface UsuarioResponseInterface{
    id:number;
    nome: string;
    email: string;
    cep: string;
    tipoUsuario:string;
    roleUsuario:string;
}

export interface LoginTokenResponseInterface{
    token:string;
    expiresIn: number
}