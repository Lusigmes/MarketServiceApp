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


export const corVinculoDemandaProposta = (
  statusDemanda: StatusDemanda, 
  statusProposta: StatusProposta,
  propostaAceitaId: number | null,
  prestadorIdProposta?: number,
  prestadorIdAtual?: number
): string => {
  // Se é o prestador responsável pela proposta aceita
  const ehPrestadorResponsavel = propostaAceitaId && prestadorIdProposta === prestadorIdAtual;
  
  if (statusDemanda === StatusDemanda.EM_ANDAMENTO && ehPrestadorResponsavel) {
    if (statusProposta === StatusProposta.ACEITA) {
      return 'yellow-lighten-5'; // Amarelo claro - em andamento
    } else if (statusProposta === StatusProposta.CONCLUIDA) {
      return 'green-lighten-5'; // Verde claro - concluído
    }
  }
  
  // Retorna a cor padrão baseada no status da demanda
  return corStatus(statusDemanda);
};

export const corVinculoPropostaDemanda = (
  statusProposta: StatusProposta,
  statusDemanda: StatusDemanda,
  demandaId?: number,
  propostaAceitaId?: number | null
): string => {
  // Se esta proposta é a aceita na demanda
  const ehPropostaAceita = propostaAceitaId === demandaId;
  
  if (ehPropostaAceita && statusDemanda === StatusDemanda.EM_ANDAMENTO) {
    if (statusProposta === StatusProposta.ACEITA) {
      return 'yellow-lighten-5'; // Amarelo claro - em andamento
    } else if (statusProposta === StatusProposta.CONCLUIDA) {
      return 'green-lighten-5'; // Verde claro - concluído
    }
  }
  
  // Retorna a cor padrão baseada no status da proposta
  return corStatusProposta(statusProposta);
};