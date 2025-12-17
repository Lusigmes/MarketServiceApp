import type { PrioridadeDemanda, StatusDemanda, StatusProposta } from "./enums";

// registro e login de usuario
export interface RegistroUsuarioInterface{
    nome: string;
    cpf: string;
    email: string;
    senha: string;
    cep: string;
    tipoUsuario: "CLIENTE" | "PRESTADOR";
    telefone?:string;
    especializacao?:string;

}

export interface LoginUsuarioInterface{
    email: string;
    senha: string;
}
export interface ClienteResponseInterface{
    id:number;
    usuario: UsuarioResponseInterface;
}
export interface PrestadorResponseInterface{
    id:number;
    usuario: UsuarioResponseInterface;
    telefone?: string;
    especializacao?:string;
}

export interface UsuarioResponseInterface{
    id:number;
    nome: string;
    email: string;
    cep: string;
    tipoUsuario:string;
    roleUsuario:string;
    telefone?:string;
    especializacao?:string;

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

export interface AvaliacaoResponseInterface {
    id: number;
    nota: number;
    comentario: string;
    clienteId: number;
    prestadorId: number;
    dataAvaliacao: string;
    nomeCliente?: string;
    nomePrestador?: string;
    demandaId:number;
}

export interface AvaliacaoInterface {
    nota: number;
    comentario: string;
    clienteId: number;
    prestadorId: number;
    demandaId:number;
}

export interface ChatMessageResponseInterface{
    id: number;
    tipo: 'CHAT' | 'SYSTEM' | 'JOIN' | 'READ' | 'ERROR';
    clienteId: number;
    clienteNome: string;
    prestadorId: number;
    prestadorNome: string;
    demandaId: number;
    demandaTitulo: string;
    conteudo: string;
    enviadoPorCliente:boolean;
    lida:boolean;
    dataEnvio: string;
}

export interface ChatMessageInterface{
    messageId?: number;
    tipo: 'CHAT' | 'SYSTEM' | 'JOIN' | 'READ' | 'ERROR';
    clienteId: number;
    prestadorId: number;
    demandaId: number;
    conteudo: string;
    enviadoPorCliente:boolean;
    destinatarioSessionId?: string;
}

export interface WebSocketInterface{
    tipo: 'CHAT' | 'SYSTEM' | 'JOIN' | 'READ' | 'ERROR';
    data:any;
    timestamp: string;
}

export interface ChatConversationInterface{
    clienteId: number;
    prestadorId: number;
    demandaId: number;
    demandaTitulo: string;
    ultimaMensagem: ChatMessageResponseInterface | null;
    mensagensNaoLidas: number;
    interlocutorNome: string;
    interlocutorId: number;
}