import { StatusDemanda, PrioridadeDemanda, StatusProposta } from "@/types/enums";

export const labelStatus = (status: StatusDemanda) => {
    switch(status){
        case StatusDemanda.ABERTA: return 'Aberta';
        case StatusDemanda.EM_ANDAMENTO: return 'Em Andamento';
        case StatusDemanda.CONCLUIDA: return 'Concluída';
        case StatusDemanda.CANCELADA: return 'Cancelada';
        default: return status;
    }
};

export const labelPrioridade = (prioridade: PrioridadeDemanda) => {
    switch(prioridade){
        case PrioridadeDemanda.BAIXA: return 'Baixa';
        case PrioridadeDemanda.MEDIA: return 'Média';
        case PrioridadeDemanda.ALTA: return 'Alta';
        default: return prioridade;
    }
};


export const corStatus = (status: StatusDemanda) => {
  switch (status) {
    case StatusDemanda.ABERTA: return 'blue-lighten-4';
    case StatusDemanda.EM_ANDAMENTO: return 'amber-lighten-4';
    case StatusDemanda.CONCLUIDA: return 'green-lighten-4';
    case StatusDemanda.CANCELADA: return 'red-lighten-4';
    default: return 'grey-lighten-3';
  }
};

export const corPrioridade = (prioridade: PrioridadeDemanda) => {
  switch (prioridade) {
    case PrioridadeDemanda.BAIXA: return 'green-darken-4';
    case PrioridadeDemanda.MEDIA: return 'yellow-darken-4';
    case PrioridadeDemanda.ALTA: return 'red-darken-4';
    default: return 'grey-darken-1';
  }
};

export const corStatusProposta = (status: StatusProposta) => {
  switch(status){
    case StatusProposta.PENDENTE: return 'bg-blue-lighten-4';
    case StatusProposta.ACEITA: return 'bg-amber-lighten-4';
    case StatusProposta.CONCLUIDA: return 'bg-green-lighten-4';
    case StatusProposta.RECUSADA: return 'bg-red-lighten-4';
    case StatusProposta.CANCELADA: return 'bg-red-darken-4';
    default: return 'bg-grey-lighten-3';
  }
}


