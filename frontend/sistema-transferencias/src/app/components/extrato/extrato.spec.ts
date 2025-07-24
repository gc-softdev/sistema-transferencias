import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';

import { ExtratoComponent } from './extrato';
import { TransferenciaService } from '../../services/transferencia';
import { TransferenciaResponse } from '../../models/transferencia.model';

describe('ExtratoComponent', () => {
  let component: ExtratoComponent;
  let fixture: ComponentFixture<ExtratoComponent>;
  let transferenciaService: jasmine.SpyObj<TransferenciaService>;

  const mockTransferencias: TransferenciaResponse[] = [
    {
      id: 1,
      contaOrigem: '1234567890',
      contaDestino: '0987654321',
      valorTransferencia: 1000,
      taxaTransferencia: 12,
      dataTransferencia: '2025-07-22',
      dataAgendamento: '2025-07-21',
      diasParaTransferencia: 1
    },
    {
      id: 2,
      contaOrigem: '1111111111',
      contaDestino: '2222222222',
      valorTransferencia: 500,
      taxaTransferencia: 6,
      dataTransferencia: '2025-07-25',
      dataAgendamento: '2025-07-20',
      diasParaTransferencia: 5
    }
  ];

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('TransferenciaService', [
      'buscarTodasTransferencias'
    ]);

    await TestBed.configureTestingModule({
      imports: [
        ExtratoComponent,
        FormsModule,
        HttpClientTestingModule
      ],
      providers: [
        { provide: TransferenciaService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ExtratoComponent);
    component = fixture.componentInstance;
    transferenciaService = TestBed.inject(TransferenciaService) as jasmine.SpyObj<TransferenciaService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load transferências on init', () => {
    transferenciaService.buscarTodasTransferencias.and.returnValue(of(mockTransferencias));

    component.ngOnInit();

    expect(transferenciaService.buscarTodasTransferencias).toHaveBeenCalled();
    expect(component.transferencias).toEqual(mockTransferencias);
    expect(component.transferenciasOriginais).toEqual(mockTransferencias);
    expect(component.loading).toBeFalsy();
  });

  it('should handle loading error', () => {
    transferenciaService.buscarTodasTransferencias.and.returnValue(
      throwError(() => new Error('Erro de rede'))
    );

    component.ngOnInit();

    expect(component.loading).toBeFalsy();
    expect(component.erro).toBe('Erro ao carregar transferências. Tente novamente.');
  });

  describe('Filtros', () => {
    beforeEach(() => {
      component.transferenciasOriginais = mockTransferencias;
    });

    it('should filter by conta origem', () => {
      component.filtroContaOrigem = '1234567890';
      component.aplicarFiltros();

      expect(component.transferencias.length).toBe(1);
      expect(component.transferencias[0].contaOrigem).toBe('1234567890');
    });

    it('should filter by data inicio', () => {
      component.filtroDataInicio = '2025-07-21';
      component.aplicarFiltros();

      expect(component.transferencias.length).toBe(1);
      expect(component.transferencias[0].dataAgendamento).toBe('2025-07-21');
    });

    it('should filter by data fim', () => {
      component.filtroDataFim = '2025-07-20';
      component.aplicarFiltros();

      expect(component.transferencias.length).toBe(1);
      expect(component.transferencias[0].dataAgendamento).toBe('2025-07-20');
    });

    it('should clear all filters', () => {
      component.filtroContaOrigem = '1234567890';
      component.filtroDataInicio = '2025-07-21';
      component.filtroDataFim = '2025-07-21';

      component.limparFiltros();

      expect(component.filtroContaOrigem).toBe('');
      expect(component.filtroDataInicio).toBe('');
      expect(component.filtroDataFim).toBe('');
    });
  });

  describe('Ordenação', () => {
    beforeEach(() => {
      component.transferencias = [...mockTransferencias];
    });

    it('should sort by campo ascending', () => {
      component.ordenarPor('valorTransferencia');

      expect(component.campoOrdenacao).toBe('valorTransferencia');
      expect(component.direcaoOrdenacao).toBe('asc');
      expect(component.transferencias[0].valorTransferencia).toBe(500);
    });

    it('should toggle sort direction when clicking same campo', () => {
      component.campoOrdenacao = 'valorTransferencia';
      component.direcaoOrdenacao = 'asc';

      component.ordenarPor('valorTransferencia');

      expect(component.direcaoOrdenacao).toBe('desc');
    });

    it('should return correct sort icon', () => {
      component.campoOrdenacao = 'valorTransferencia';
      component.direcaoOrdenacao = 'asc';

      expect(component.obterIconeOrdenacao('valorTransferencia')).toBe('↑');
      expect(component.obterIconeOrdenacao('contaOrigem')).toBe('↕️');

      component.direcaoOrdenacao = 'desc';
      expect(component.obterIconeOrdenacao('valorTransferencia')).toBe('↓');
    });
  });

  describe('Formatação', () => {
    it('should format date correctly', () => {
      const dataFormatada = component.formatarData('2025-07-21');
      expect(dataFormatada).toBe('21/07/2025');
    });

    it('should format currency correctly', () => {
      const moedaFormatada = component.formatarMoeda(1000);
      expect(moedaFormatada).toContain('R$');
      expect(moedaFormatada).toContain('1.000,00');
    });
  });

  describe('Status da Transferência', () => {
    it('should return correct status for today', () => {
      const hoje = new Date().toISOString().split('T')[0];
      const status = component.obterStatusTransferencia(hoje);

      expect(status.texto).toBe('Hoje');
      expect(status.classe).toBe('bg-yellow-100 text-yellow-800');
    });

    it('should return correct status for future date', () => {
      const amanha = new Date();
      amanha.setDate(amanha.getDate() + 1);
      const dataAmanha = amanha.toISOString().split('T')[0];
      
      const status = component.obterStatusTransferencia(dataAmanha);

      expect(status.texto).toBe('Em 1 dia');
      expect(status.classe).toBe('bg-blue-100 text-blue-800');
    });

    it('should return correct status for past date', () => {
      const ontem = new Date();
      ontem.setDate(ontem.getDate() - 1);
      const dataOntem = ontem.toISOString().split('T')[0];
      
      const status = component.obterStatusTransferencia(dataOntem);

      expect(status.texto).toBe('Realizada');
      expect(status.classe).toBe('bg-green-100 text-green-800');
    });

    it('should return correct row color by status', () => {
      const hoje = new Date().toISOString().split('T')[0];
      const ontem = new Date();
      ontem.setDate(ontem.getDate() - 1);
      const dataOntem = ontem.toISOString().split('T')[0];
      const amanha = new Date();
      amanha.setDate(amanha.getDate() + 1);
      const dataAmanha = amanha.toISOString().split('T')[0];

      expect(component.obterCorLinhaPorStatus(dataOntem)).toBe('bg-green-50');
      expect(component.obterCorLinhaPorStatus(hoje)).toBe('bg-yellow-50');
      expect(component.obterCorLinhaPorStatus(dataAmanha)).toBe('bg-white');
    });
  });

  describe('Cálculos', () => {
    beforeEach(() => {
      component.transferencias = mockTransferencias;
    });

    it('should calculate total transferências', () => {
      const total = component.calcularTotalTransferencias();
      expect(total).toBe(1500); // 1000 + 500
    });

    it('should calculate total taxas', () => {
      const total = component.calcularTotalTaxas();
      expect(total).toBe(18); // 12 + 6
    });
  });

  describe('Export CSV', () => {
    beforeEach(() => {
      component.transferencias = mockTransferencias;
      // Mock do DOM para teste de download
      spyOn(document, 'createElement').and.callFake((tagName: string) => {
        if (tagName === 'a') {
          return {
            download: '',
            href: '',
            style: { visibility: '' },
            setAttribute: jasmine.createSpy('setAttribute'),
            click: jasmine.createSpy('click')
          } as any;
        }
        return document.createElement(tagName);
      });
      spyOn(document.body, 'appendChild');
      spyOn(document.body, 'removeChild');
      spyOn(URL, 'createObjectURL').and.returnValue('blob:url');
    });

    it('should export CSV when transferências exist', () => {
      component.exportarCSV();

      expect(document.createElement).toHaveBeenCalledWith('a');
      expect(URL.createObjectURL).toHaveBeenCalled();
    });

    it('should show alert when no transferências to export', () => {
      component.transferencias = [];
      spyOn(window, 'alert');

      component.exportarCSV();

      expect(window.alert).toHaveBeenCalledWith('Não há transferências para exportar.');
    });
  });

  describe('TrackBy', () => {
    it('should return transferência ID for trackBy', () => {
      const transferencia = mockTransferencias[0];
      const result = component.trackByTransferenciaId(0, transferencia);

      expect(result).toBe(transferencia.id);
    });
  });

  describe('Reload', () => {
    it('should reload transferências', () => {
      transferenciaService.buscarTodasTransferencias.and.returnValue(of(mockTransferencias));

      component.carregarTransferencias();

      expect(transferenciaService.buscarTodasTransferencias).toHaveBeenCalled();
      expect(component.transferencias).toEqual(mockTransferencias);
    });
  });
});

