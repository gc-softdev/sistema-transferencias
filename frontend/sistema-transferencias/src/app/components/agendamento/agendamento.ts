import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { TransferenciaService } from '../../services/transferencia';
import {
  TransferenciaRequest,
  TransferenciaResponse,
  TaxaCalculoResponse,
  ApiError
} from '../../models/transferencia.model';

@Component({
  selector: 'app-agendamento',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './agendamento.html',
  styleUrls: ['./agendamento.css']
})
export class AgendamentoComponent implements OnInit {
  agendamentoForm: FormGroup;
  loading = false;
  sucesso = false;
  erro: string | null = null;
  errosValidacao: { [key: string]: string } = {};
  taxaCalculada: number | null = null;
  transferenciaCriada: TransferenciaResponse | null = null;

  constructor(
    private fb: FormBuilder,
    private transferenciaService: TransferenciaService
  ) {
    this.agendamentoForm = this.createForm();
  }

  ngOnInit(): void {
    // Configurar validação em tempo real para cálculo de taxa
    this.agendamentoForm.valueChanges.subscribe(() => {
      this.limparMensagens();
      if (this.podeCalcularTaxa()) {
        this.calcularTaxaAutomaticamente();
      }
    });
  }

  private createForm(): FormGroup {
    const hoje = new Date();
    return this.fb.group({
      contaOrigem: ['', [
        Validators.required,
        Validators.pattern(/^\d{10}$/),
        this.contasDiferentesValidator.bind(this)
      ]],
      contaDestino: ['', [
        Validators.required,
        Validators.pattern(/^\d{10}$/),
        this.contasDiferentesValidator.bind(this)
      ]],
      valorTransferencia: ['', [
        Validators.required,
        Validators.min(0.01),
        this.valorMinimoValidator.bind(this)
      ]],
      dataTransferencia: [this.formatarData(hoje), [
        Validators.required,
        this.dataFuturaValidator
      ]]
    });
  }

  private contasDiferentesValidator(control: AbstractControl) {
    if (!control.value) return null;

    const form = control.parent;
    if (!form) return null;

    const contaOrigem = form.get('contaOrigem')?.value;
    const contaDestino = form.get('contaDestino')?.value;

    if (contaOrigem && contaDestino && contaOrigem === contaDestino) {
      return { contasIguais: true };
    }

    return null;
  }

  private dataFuturaValidator(control: AbstractControl) {
    if (!control.value) return null;

    const dataInformadaStr = control.value;
    const hojeStr = new Date().toISOString().split('T')[0];


    if (dataInformadaStr < hojeStr) {
      return { dataPassada: true };
    }

    return null;
  }

  private valorMinimoValidator(control: AbstractControl) {
    if (!control.value) return null;

    const form = control.parent;
    if (!form) return null;

    const valor = control.value;
    const dataTransferencia = form.get('dataTransferencia')?.value;

    if (dataTransferencia) {
      const hojeStr = new Date().toISOString().split('T')[0];
      const dataTransferenciaStr = dataTransferencia;

      const hoje = new Date(hojeStr + 'T00:00:00');
      const dataInformada = new Date(dataTransferenciaStr + 'T00:00:00');
      const dias = Math.floor((dataInformada.getTime() - hoje.getTime()) / (1000 * 60 * 60 * 24));

      if (dias === 0 && valor < 3.00) {
        return { valorMinimoDiaZero: true };
      }

      if (dias >= 1 && dias <= 10 && valor < 12.00) {
        return { valorMinimoDias1a10: true };
      }
    }

    return null;
  }

  private formatarData(data: Date): string {
    return data.toISOString().split('T')[0];
  }

  private podeCalcularTaxa(): boolean {
    const form = this.agendamentoForm;
    const valorValido = form.get('valorTransferencia')?.valid ?? false;
    const dataValida = form.get('dataTransferencia')?.valid ?? false;
    const valorMaiorQueZero = (form.get('valorTransferencia')?.value ?? 0) > 0;

    return valorValido && dataValida && valorMaiorQueZero;
  }

  private calcularTaxaAutomaticamente(): void {
    const valor = this.agendamentoForm.get('valorTransferencia')?.value;
    const data = this.agendamentoForm.get('dataTransferencia')?.value;

    if (valor && data && this.transferenciaService) {
      this.transferenciaService.calcularTaxa(valor, data).subscribe({
        next: (response: TaxaCalculoResponse) => {
          this.taxaCalculada = response.taxaCalculada;
        },
        error: (error) => {
          this.taxaCalculada = null;
          if (error.error?.message) {
            this.erro = error.error.message;
            if (error.error.message.includes("não há taxa percentual aplicável")) {
              this.agendamentoForm.get('dataTransferencia')?.setErrors({ taxaNaoAplicavel: true });
            }
          }
        }
      });
    }
  }

  onSubmit(): void {
    if (this.agendamentoForm.valid) {
      this.loading = true;
      this.limparMensagens();

      const transferencia: TransferenciaRequest = {
        contaOrigem: this.agendamentoForm.get('contaOrigem')?.value,
        contaDestino: this.agendamentoForm.get('contaDestino')?.value,
        valorTransferencia: this.agendamentoForm.get('valorTransferencia')?.value,
        dataTransferencia: this.agendamentoForm.get('dataTransferencia')?.value
      };

      this.transferenciaService.agendarTransferencia(transferencia).subscribe({
        next: (response: TransferenciaResponse) => {
          this.sucesso = true;
          this.transferenciaCriada = response;
          this.agendamentoForm.reset();
          this.taxaCalculada = null;
          this.loading = false;

          const hoje = new Date();
          this.agendamentoForm.patchValue({
            dataTransferencia: this.formatarData(hoje)
          });
        },
        error: (error) => {
          this.loading = false;
          this.tratarErro(error);
        }
      });
    } else {
      this.marcarCamposComoTocados();
    }
  }

  private tratarErro(error: any): void {
    if (error.error) {
      const apiError: ApiError = error.error;

      if (apiError.errors) {
        // Erros de validação
        this.errosValidacao = apiError.errors;
      } else {
        // Erro geral
        this.erro = apiError.message || 'Erro ao agendar transferência';
        if (apiError.message?.includes("não há taxa percentual aplicável")) {
          this.agendamentoForm.get('dataTransferencia')?.setErrors({ taxaNaoAplicavel: true });
        }
      }
    } else {
      this.erro = 'Erro de conexão com o servidor';
    }
  }

  private marcarCamposComoTocados(): void {
    Object.keys(this.agendamentoForm.controls).forEach(key => {
      this.agendamentoForm.get(key)?.markAsTouched();
    });
  }

  private limparMensagens(): void {
    this.erro = null;
    this.errosValidacao = {};
    this.sucesso = false;
  }

  novaTransferencia(): void {
    this.sucesso = false;
    this.transferenciaCriada = null;
    this.limparMensagens();
  }

  get contaOrigem() { return this.agendamentoForm.get('contaOrigem'); }
  get contaDestino() { return this.agendamentoForm.get('contaDestino'); }
  get valorTransferencia() { return this.agendamentoForm.get('valorTransferencia'); }
  get dataTransferencia() { return this.agendamentoForm.get('dataTransferencia'); }

  temErro(campo: string, erro: string): boolean {
    const control = this.agendamentoForm.get(campo);
    return !!(control?.hasError(erro) && (control?.dirty || control?.touched));
  }

  temErroValidacao(campo: string): boolean {
    return !!this.errosValidacao[campo];
  }

  obterMensagemErro(campo: string): string {
    if (this.errosValidacao[campo]) {
      return this.errosValidacao[campo];
    }

    const control = this.agendamentoForm.get(campo);
    if (control?.errors && (control?.dirty || control?.touched)) {
      if (control.errors['required']) return `${this.obterNomeCampo(campo)} é obrigatório`;
      if (control.errors['pattern']) return `${this.obterNomeCampo(campo)} deve ter exatamente 10 dígitos`;
      if (control.errors['min']) return 'Valor deve ser maior que zero';
      if (control.errors['contasIguais']) return 'Conta de origem e destino devem ser diferentes';
      if (control.errors['dataPassada']) return 'Data deve ser igual ou futura';
      if (control.errors['valorMinimoDiaZero']) return 'Valor deve ser maior ou igual a R$ 3,00 para transferências no mesmo dia';
      if (control.errors['valorMinimoDias1a10']) return 'Valor deve ser maior ou igual a R$ 12,00 para transferências de 1 a 10 dias';
      if (control.errors['taxaNaoAplicavel']) return 'Transferência negada, não há taxa percentual aplicável para esta data';
    }

    return '';
  }

  private obterNomeCampo(campo: string): string {
    const nomes: { [key: string]: string } = {
      'contaOrigem': 'Conta de origem',
      'contaDestino': 'Conta de destino',
      'valorTransferencia': 'Valor da transferência',
      'dataTransferencia': 'Data da transferência'
    };
    return nomes[campo] || campo;
  }
}
