export interface TransferenciaRequest {
  contaOrigem: string;
  contaDestino: string;
  valorTransferencia: number;
  dataTransferencia: string;
}

export interface TransferenciaResponse {
  id: number;
  contaOrigem: string;
  contaDestino: string;
  valorTransferencia: number;
  taxaTransferencia: number;
  dataTransferencia: string;
  dataAgendamento: string;
  diasParaTransferencia: number;
}

export interface TaxaCalculoResponse {
  valorTransferencia: number;
  dataTransferencia: string;
  taxaCalculada: number;
  diasParaTransferencia: number;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  errors?: { [key: string]: string };
}

