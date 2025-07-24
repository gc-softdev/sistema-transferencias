import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { AgendamentoComponent } from './agendamento';
import { TransferenciaService } from '../../services/transferencia';
import { TransferenciaResponse, TaxaCalculoResponse } from '../../models/transferencia.model';

describe('AgendamentoComponent', () => {
  let component: AgendamentoComponent;
  let fixture: ComponentFixture<AgendamentoComponent>;
  let transferenciaService: jasmine.SpyObj<TransferenciaService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('TransferenciaService', [
      'agendarTransferencia',
      'calcularTaxa'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        AgendamentoComponent,
        ReactiveFormsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: TransferenciaService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AgendamentoComponent);
    component = fixture.componentInstance;
    transferenciaService = TestBed.inject(TransferenciaService) as jasmine.SpyObj<TransferenciaService>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with default values', () => {
    expect(component.agendamentoForm).toBeDefined();
    expect(component.agendamentoForm.get('contaOrigem')?.value).toBe('');
    expect(component.agendamentoForm.get('contaDestino')?.value).toBe('');
    expect(component.agendamentoForm.get('valorTransferencia')?.value).toBe('');
    const hoje = new Date().toISOString().split('T')[0]; // "2025-07-22"
    expect(component.agendamentoForm.get('dataTransferencia')?.value).toBe(hoje);
  });

  describe('Form Validation', () => {
    it('should validate conta origem format', () => {
      const contaOrigem = component.agendamentoForm.get('contaOrigem');

      contaOrigem?.setValue('123');
      expect(contaOrigem?.hasError('pattern')).toBeTruthy();

      contaOrigem?.setValue('1234567890');
      expect(contaOrigem?.hasError('pattern')).toBeFalsy();
    });

    it('should validate conta destino format', () => {
      const contaDestino = component.agendamentoForm.get('contaDestino');

      contaDestino?.setValue('123456789012345');
      expect(contaDestino?.hasError('pattern')).toBeTruthy();

      contaDestino?.setValue('0987654321');
      expect(contaDestino?.hasError('pattern')).toBeFalsy();
    });

    it('should validate valor transferencia minimo para dia zero', () => {
      const valorTransferencia = component.agendamentoForm.get('valorTransferencia');
      const dataTransferencia = component.agendamentoForm.get('dataTransferencia');
      const hoje = new Date().toISOString().split('T')[0];

      dataTransferencia?.setValue(hoje);
      valorTransferencia?.setValue(2.99);
      expect(valorTransferencia?.hasError('valorMinimoDiaZero')).toBeTruthy();

      valorTransferencia?.setValue(3.00);
      expect(valorTransferencia?.hasError('valorMinimoDiaZero')).toBeFalsy();
    });

    it('should validate valor transferencia minimo para 1 a 10 dias', () => {
      const valorTransferencia = component.agendamentoForm.get('valorTransferencia');
      const dataTransferencia = component.agendamentoForm.get('dataTransferencia');
      const cincoDias = new Date();
      cincoDias.setDate(cincoDias.getDate() + 5);

      dataTransferencia?.setValue(cincoDias.toISOString().split('T')[0]);
      valorTransferencia?.setValue(11.99);
      expect(valorTransferencia?.hasError('valorMinimoDias1a10')).toBeTruthy();

      valorTransferencia?.setValue(12.00);
      expect(valorTransferencia?.hasError('valorMinimoDias1a10')).toBeFalsy();
    });

    it('should validate that contas are different', () => {
      component.agendamentoForm.patchValue({
        contaOrigem: '1234567890',
        contaDestino: '1234567890'
      });

      const contaOrigem = component.agendamentoForm.get('contaOrigem');
      const contaDestino = component.agendamentoForm.get('contaDestino');

      expect(contaOrigem?.hasError('contasIguais')).toBeTruthy();
      expect(contaDestino?.hasError('contasIguais')).toBeTruthy();
    });

    it('should validate future date', () => {
      const dataTransferencia = component.agendamentoForm.get('dataTransferencia');
      const ontem = new Date();
      ontem.setDate(ontem.getDate() - 1);
      const hoje = new Date().toISOString().split('T')[0];

      dataTransferencia?.setValue(ontem.toISOString().split('T')[0]);
      expect(dataTransferencia?.hasError('dataPassada')).toBeTruthy();

      dataTransferencia?.setValue(hoje);
      expect(dataTransferencia?.hasError('dataPassada')).toBeFalsy();
    });
  });

  describe('Taxa Calculation', () => {
    it('should calculate taxa automatically when form is valid for dia zero', () => {
      const mockTaxaResponse: TaxaCalculoResponse = {
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22',
        taxaCalculada: 25,
        diasParaTransferencia: 0
      };

      transferenciaService.calcularTaxa.and.returnValue(of(mockTaxaResponse));

      component.agendamentoForm.patchValue({
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22'
      });


      component.agendamentoForm.updateValueAndValidity();

      expect(transferenciaService.calcularTaxa).toHaveBeenCalledWith(1000, '2025-07-22');
      expect(component.taxaCalculada).toBe(25);
    });

    it('should handle taxa calculation error for 1 a 10 dias', () => {
      transferenciaService.calcularTaxa.and.returnValue(
        throwError(() => ({ error: { message: 'Transferência negada, não há taxa percentual aplicável para esta data' } }))
      );

      component.agendamentoForm.patchValue({
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 12,
        dataTransferencia: '2025-07-27' // 5 dias
      });

      component.agendamentoForm.updateValueAndValidity();

      expect(component.taxaCalculada).toBeNull();
      expect(component.erro).toBe('Transferência negada, não há taxa percentual aplicável para esta data');
      expect(component.agendamentoForm.get('dataTransferencia')?.hasError('taxaNaoAplicavel')).toBeTruthy();
    });
  });

  describe('Form Submission', () => {
    it('should submit form when valid', () => {
      const mockResponse: TransferenciaResponse = {
        id: 1,
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        taxaTransferencia: 25,
        dataTransferencia: '2025-07-22',
        dataAgendamento: '2025-07-22',
        diasParaTransferencia: 0
      };

      transferenciaService.agendarTransferencia.and.returnValue(of(mockResponse));

      component.agendamentoForm.patchValue({
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22'
      });

      component.onSubmit();

      expect(transferenciaService.agendarTransferencia).toHaveBeenCalled();
      expect(component.sucesso).toBeTruthy();
      expect(component.transferenciaCriada).toEqual(mockResponse);
    });

    it('should not submit form when invalid', () => {
      component.agendamentoForm.patchValue({
        contaOrigem: '123', // Inválido
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22'
      });

      component.onSubmit();

      expect(transferenciaService.agendarTransferencia).not.toHaveBeenCalled();
      expect(component.sucesso).toBeFalsy();
    });

    it('should handle submission error for 1 a 10 dias', () => {
      transferenciaService.agendarTransferencia.and.returnValue(
        throwError(() => ({
          error: {
            message: 'Transferência negada, não há taxa percentual aplicável para esta data'
          }
        }))
      );

      component.agendamentoForm.patchValue({
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 12,
        dataTransferencia: '2025-07-27'
      });

      component.onSubmit();

      expect(component.loading).toBeFalsy();
      expect(component.erro).toBe('Transferência negada, não há taxa percentual aplicável para esta data');
      expect(component.agendamentoForm.get('dataTransferencia')?.hasError('taxaNaoAplicavel')).toBeTruthy();
    });

    it('should handle submission error for valor abaixo do mínimo', () => {
      transferenciaService.agendarTransferencia.and.returnValue(
        throwError(() => ({
          error: {
            message: 'Valor da transferência deve ser maior ou igual a R$ 12,00 para transferências de 1 a 10 dias'
          }
        }))
      );

      component.agendamentoForm.patchValue({
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 11.99,
        dataTransferencia: '2025-07-27'
      });

      component.onSubmit();

      expect(component.loading).toBeFalsy();
      expect(component.erro).toBe('Valor da transferência deve ser maior ou igual a R$ 12,00 para transferências de 1 a 10 dias');
    });
  });

  describe('Helper Methods', () => {
    it('should return correct error messages', () => {
      const contaOrigem = component.agendamentoForm.get('contaOrigem');
      contaOrigem?.setValue('');
      contaOrigem?.markAsTouched();

      expect(component.obterMensagemErro('contaOrigem')).toBe('Conta de origem é obrigatório');

      const valorTransferencia = component.agendamentoForm.get('valorTransferencia');
      const dataTransferencia = component.agendamentoForm.get('dataTransferencia');
      dataTransferencia?.setValue('2025-07-22');
      valorTransferencia?.setValue(2.99);
      valorTransferencia?.markAsTouched();
      expect(component.obterMensagemErro('valorTransferencia'))
          .toBe('Valor deve ser maior ou igual a R$ 3,00 para transferências no mesmo dia');

      dataTransferencia?.setValue('2025-07-27'); // 5 dias
      valorTransferencia?.setValue(11.99);
      expect(component.obterMensagemErro('valorTransferencia'))
          .toBe('Valor deve ser maior ou igual a R$ 12,00 para transferências de 1 a 10 dias');

      dataTransferencia?.setErrors({ taxaNaoAplicavel: true });
      dataTransferencia?.markAsTouched();
      expect(component.obterMensagemErro('dataTransferencia'))
          .toBe('Transferência negada, não há taxa percentual aplicável para esta data');
    });

    it('should check if field has specific error', () => {
      const contaOrigem = component.agendamentoForm.get('contaOrigem');
      contaOrigem?.setValue('');
      contaOrigem?.markAsTouched();

      expect(component.temErro('contaOrigem', 'required')).toBeTruthy();
      expect(component.temErro('contaOrigem', 'pattern')).toBeFalsy();
    });

    it('should reset form for new transferência', () => {
      component.sucesso = true;
      component.transferenciaCriada = {} as TransferenciaResponse;

      component.novaTransferencia();

      expect(component.sucesso).toBeFalsy();
      expect(component.transferenciaCriada).toBeNull();
    });
  });

  describe('Getters', () => {
    it('should return form controls via getters', () => {
      expect(component.contaOrigem).toBe(component.agendamentoForm.get('contaOrigem'));
      expect(component.contaDestino).toBe(component.agendamentoForm.get('contaDestino'));
      expect(component.valorTransferencia).toBe(component.agendamentoForm.get('valorTransferencia'));
      expect(component.dataTransferencia).toBe(component.agendamentoForm.get('dataTransferencia'));
    });
  });
});
