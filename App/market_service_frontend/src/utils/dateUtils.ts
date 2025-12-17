// Converte yyyy-mm-dd para dd/mm/yyyy
export const formatarDataBr = (dataISO: string): string => {
  if (!dataISO) return '';
  const [ano, mes, dia] = dataISO.split('-');
  return `${dia.padStart(2, '0')}/${mes.padStart(2, '0')}/${ano}`;
};

// Converte dd/mm/yyyy para yyyy-mm-dd
export const formatarDataISO = (dataBr: string): string => {
  if (!dataBr) return '';
  const [dia, mes, ano] = dataBr.split('/');
  return `${ano}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}`;
};

// Validação data br (similar ao CPF/CEP)
export const validarDataBr = (dataBr: string): boolean => {
  const regex = /^\d{2}\/\d{2}\/\d{4}$/;
  if (!regex.test(dataBr)) return false;
  
  const [diaStr, mesStr, anoStr] = dataBr.split('/');
  const dia = parseInt(diaStr, 10);
  const mes = parseInt(mesStr, 10);
  const ano = parseInt(anoStr, 10);
  
  // Verifica valores básicos
  if (dia < 1 || dia > 31) return false;
  if (mes < 1 || mes > 12) return false;
  if (ano < 1900 || ano > 2100) return false;
  
  // Verifica meses com 30 dias
  if ([4, 6, 9, 11].includes(mes) && dia > 30) return false;
  
  // Verifica fevereiro
  if (mes === 2) {
    const isBissexto = (ano % 4 === 0 && ano % 100 !== 0) || (ano % 400 === 0);
    if (dia > (isBissexto ? 29 : 28)) return false;
  }
  
  const data = new Date(ano, mes - 1, dia);
  return data.getDate() === dia && 
         data.getMonth() === mes - 1 && 
         data.getFullYear() === ano;
};

export const validarDataFutura = (dataBr: string): boolean => {
  if (!validarDataBr(dataBr)) return false;
  
  const [dia, mes, ano] = dataBr.split('/').map(Number);
  const dataInput = new Date(ano, mes - 1, dia);
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  
  return dataInput >= hoje;
};

// Extrai dia, mês e ano de uma data BR
export const extrairPartesDataBr = (dataBr: string): { dia: string, mes: string, ano: string } | null => {
  if (!dataBr) return null;
  
  const cleanValue = dataBr.replace(/\D/g, '');
  if (cleanValue.length !== 8) return null;
  
  return {
    dia: cleanValue.substring(0, 2),
    mes: cleanValue.substring(2, 4),
    ano: cleanValue.substring(4, 8)
  };
};

// string no formato ISO (yyyy-MM-dd)
export const isValidISOFormat = (data: string): boolean => {
  return /^\d{4}-\d{2}-\d{2}$/.test(data);
};

export function formatarDataParaISO(data: string): string {
  if (!data) return '';
  
  if (isValidISOFormat(data)) {
    return data;
  }
  
  // Se estiver no formato BR (dd/MM/yyyy), converte para ISO
  if (data.includes('/')) {
    const [dia, mes, ano] = data.split('/');
    return `${ano}-${mes.padStart(2, '0')}-${dia.padStart(2, '0')}`;
  }
  return '';
}


export const parsearData = (data: any): Date => {
  if (!data) return new Date();
  
  // Se já for um objeto Date
  if (data instanceof Date) return data;
  
  // Se for string em formato ISO (yyyy-MM-dd)
  if (typeof data === 'string' && isValidISOFormat(data)) {
    return new Date(data);
  }
  
  // Se for string com timestamp
  if (typeof data === 'string' && !isNaN(Date.parse(data))) {
    return new Date(data);
  }
  
  // Se for número (timestamp)
  if (typeof data === 'number') {
    return new Date(data);
  }
  
  // Tenta converter de string BR (dd/MM/yyyy)
  if (typeof data === 'string' && data.includes('/')) {
    const [dia, mes, ano] = data.split('/');
    return new Date(parseInt(ano), parseInt(mes) - 1, parseInt(dia));
  }
  
  console.warn('Formato de data não reconhecido:', data);
  return new Date();
};

export const formatarDataParaExibicao = (data: any): string => {
  try {
    const dataObj = parsearData(data);
    return dataObj.toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  } catch (error) {
    console.error('Erro ao formatar data:', data, error);
    return 'Data inválida';
  }
};

