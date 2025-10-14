import type { PrioridadeDemanda, StatusDemanda, StatusProposta } from "./enums";

// registro e login de usuario
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
export interface ClienteResponseInterface{
    id:number;
    usuario: UsuarioResponseInterface;
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


// relações

export interface DemandaResponseInterface{
    id: number;
    titulo: string;
    descricao: string;
    categoria: string;
    localizacao: string;
    prazo: string;
    orcamentoEstimado: number;
    prioridade: PrioridadeDemanda;
    statusDemanda: StatusDemanda;
    dataCriacao: string;
    ultimaAtualizacao: string;
    clienteId: number | null;
    propostaAceitaId: number | null;
}

export interface PropostaResponseInterface{
    id: number,
    titulo: string,
    preco: number,
    comentario: string,
    statusProposta: StatusProposta,
    dataCriacao: string,
    dataAtualizacao: string,
    demandaId: number,
    prestadorId: number | null
}

export interface Notificacao {
  id: number;
  mensagem: string;
  lida: boolean;
  dataCriacaoNotificacao: string;
}

