import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransferenciaService } from '../../services/transferencia';
import { TransferenciaResponse } from '../../models/transferencia.model';

@Component({
  selector: 'app-extrato',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './extrato.html',
  styleUrls: ['./extrato.css']
})
export class ExtratoComponent implements OnInit {
  transferencias: TransferenciaResponse[] = [];
  transferenciasOriginais: TransferenciaResponse[] = [];
  loading = false;
  erro: string | null = null;

  // Filtros
  filtroContaOrigem = '';
  filtroDataInicio = '';
  filtroDataFim = '';

  // Ordenação
  campoOrdenacao: keyof TransferenciaResponse = 'dataAgendamento';
  direcaoOrdenacao: 'asc' | 'desc' = 'desc';

  constructor(private transferenciaService: TransferenciaService) {}

  ngOnInit(): void {
    this.carregarTransferencias();
  }

  carregarTransferencias(): void {
    this.loading = true;
    this.erro = null;

    this.transferenciaService.buscarTodasTransferencias().subscribe({
      next: (transferencias: TransferenciaResponse[]) => {
        this.transferenciasOriginais = transferencias;
        this.aplicarFiltros();
        this.loading = false;
      },
      error: (error) => {
        this.loading = false;
        this.erro = 'Erro ao carregar transferências. Tente novamente.';
        console.error('Erro ao carregar transferências:', error);
      }
    });
  }

  aplicarFiltros(): void {
    let transferenciasFiltradas = [...this.transferenciasOriginais];

    // Filtro por conta de origem
    if (this.filtroContaOrigem.trim()) {
      transferenciasFiltradas = transferenciasFiltradas.filter(t =>
        t.contaOrigem.includes(this.filtroContaOrigem.trim())
      );
    }

    // Filtro por data de agendamento
    if (this.filtroDataInicio) {
      transferenciasFiltradas = transferenciasFiltradas.filter(t =>
        t.dataAgendamento >= this.filtroDataInicio
      );
    }

    if (this.filtroDataFim) {
      transferenciasFiltradas = transferenciasFiltradas.filter(t =>
        t.dataAgendamento <= this.filtroDataFim
      );
    }

    this.transferencias = transferenciasFiltradas;
    this.ordenarTransferencias();
  }

  limparFiltros(): void {
    this.filtroContaOrigem = '';
    this.filtroDataInicio = '';
    this.filtroDataFim = '';
    this.aplicarFiltros();
  }

  ordenarPor(campo: keyof TransferenciaResponse): void {
    if (this.campoOrdenacao === campo) {
      this.direcaoOrdenacao = this.direcaoOrdenacao === 'asc' ? 'desc' : 'asc';
    } else {
      this.campoOrdenacao = campo;
      this.direcaoOrdenacao = 'asc';
    }
    this.ordenarTransferencias();
  }

  private ordenarTransferencias(): void {
    this.transferencias.sort((a, b) => {
      const valorA = a[this.campoOrdenacao];
      const valorB = b[this.campoOrdenacao];

      let comparacao = 0;

      if (typeof valorA === 'string' && typeof valorB === 'string') {
        comparacao = valorA.localeCompare(valorB);
      } else if (typeof valorA === 'number' && typeof valorB === 'number') {
        comparacao = valorA - valorB;
      } else {
        comparacao = String(valorA).localeCompare(String(valorB));
      }

      return this.direcaoOrdenacao === 'asc' ? comparacao : -comparacao;
    });
  }

  obterIconeOrdenacao(campo: keyof TransferenciaResponse): string {
    if (this.campoOrdenacao !== campo) {
      return '↕️';
    }
    return this.direcaoOrdenacao === 'asc' ? '↑' : '↓';
  }

  formatarData(data: string): string {
    // Normaliza a data para evitar problemas de fuso horário
    const [ano, mes, dia] = data.split('-').map(Number);
    const dataNormalizada = new Date(ano, mes - 1, dia);
    return dataNormalizada.toLocaleDateString('pt-BR');
  }

  formatarMoeda(valor: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(valor);
  }

  obterStatusTransferencia(dataTransferencia: string): { texto: string; classe: string } {
    // Normaliza a data de transferência para considerar apenas a data (YYYY-MM-DD)
    const [ano, mes, dia] = dataTransferencia.split('-').map(Number);
    const dataTransf = new Date(ano, mes - 1, dia);

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    dataTransf.setHours(0, 0, 0, 0);

    if (dataTransf > hoje) {
      const dias = Math.ceil((dataTransf.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24));
      return {
        texto: `Em ${dias} dia${dias > 1 ? 's' : ''}`,
        classe: 'bg-blue-100 text-blue-800'
      };
    } else {
      return { texto: 'Realizada', classe: 'bg-green-100 text-green-800' };
    }
  }

  obterCorLinhaPorStatus(dataTransferencia: string): string {
    const status = this.obterStatusTransferencia(dataTransferencia);

    if (status.texto === 'Realizada') {
      return 'bg-green-50';
    }

    return 'bg-white';
  }

  calcularTotalTransferencias(): number {
    return this.transferencias.reduce((total, t) => total + t.valorTransferencia, 0);
  }

  calcularTotalTaxas(): number {
    return this.transferencias.reduce((total, t) => total + t.taxaTransferencia, 0);
  }

  exportarCSV(): void {
    if (this.transferencias.length === 0) {
      alert('Não há transferências para exportar.');
      return;
    }

    const headers = [
      'ID',
      'Conta Origem',
      'Conta Destino',
      'Valor Transferência',
      'Taxa',
      'Data Agendamento',
      'Data Transferência',
      'Dias para Transferência'
    ];

    const csvContent = [
      headers.join(','),
      ...this.transferencias.map(t => [
        t.id,
        t.contaOrigem,
        t.contaDestino,
        t.valorTransferencia.toFixed(2).replace('.', ','),
        t.taxaTransferencia.toFixed(2).replace('.', ','),
        this.formatarData(t.dataAgendamento),
        this.formatarData(t.dataTransferencia),
        t.diasParaTransferencia
      ].join(','))
    ].join('\n');

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');

    if (link.download !== undefined) {
      const url = URL.createObjectURL(blob);
      link.setAttribute('href', url);
      link.setAttribute('download', `extrato_transferencias_${new Date().toISOString().split('T')[0]}.csv`);
      link.style.visibility = 'hidden';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    }
  }

  trackByTransferenciaId(index: number, transferencia: TransferenciaResponse): number {
    return transferencia.id;
  }
}
