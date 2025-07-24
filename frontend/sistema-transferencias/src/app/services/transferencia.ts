import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  TransferenciaRequest,
  TransferenciaResponse,
  TaxaCalculoResponse
} from '../models/transferencia.model';

@Injectable({
  providedIn: 'root'
})
export class TransferenciaService {
  private readonly apiUrl = 'http://localhost:8080/api/transferencias';

  constructor(private http: HttpClient) { }


   // Agenda uma nova transferência


  agendarTransferencia(transferencia: TransferenciaRequest): Observable<TransferenciaResponse> {
    return this.http.post<TransferenciaResponse>(this.apiUrl, transferencia);
  }


   // Busca todas as transferências agendadas

  buscarTodasTransferencias(): Observable<TransferenciaResponse[]> {
    return this.http.get<TransferenciaResponse[]>(this.apiUrl);
  }


   // Busca uma transferência por ID

  buscarTransferenciaPorId(id: number): Observable<TransferenciaResponse> {
    return this.http.get<TransferenciaResponse>(`${this.apiUrl}/${id}`);
  }


   // Busca transferências por conta de origem

  buscarTransferenciasPorConta(contaOrigem: string): Observable<TransferenciaResponse[]> {
    return this.http.get<TransferenciaResponse[]>(`${this.apiUrl}/conta/${contaOrigem}`);
  }


   // Calcula a taxa para uma transferência sem agendá-la

  calcularTaxa(valor: number, dataTransferencia: string): Observable<TaxaCalculoResponse> {
    const params = new HttpParams()
      .set('valor', valor.toString())
      .set('dataTransferencia', dataTransferencia);

    return this.http.get<TaxaCalculoResponse>(`${this.apiUrl}/calcular-taxa`, { params });
  }


   // Verifica o status da API

  verificarStatus(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }
}

