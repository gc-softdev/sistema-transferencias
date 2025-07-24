import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransferenciaService } from './transferencia';
import { 
  TransferenciaRequest, 
  TransferenciaResponse, 
  TaxaCalculoResponse 
} from '../models/transferencia.model';

describe('TransferenciaService', () => {
  let service: TransferenciaService;
  let httpMock: HttpTestingController;
  const apiUrl = 'http://localhost:8080/api/transferencias';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransferenciaService]
    });
    service = TestBed.inject(TransferenciaService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('agendarTransferencia', () => {
    it('should send POST request to agenda transferência', () => {
      const mockRequest: TransferenciaRequest = {
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22'
      };

      const mockResponse: TransferenciaResponse = {
        id: 1,
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        taxaTransferencia: 12,
        dataTransferencia: '2025-07-22',
        dataAgendamento: '2025-07-21',
        diasParaTransferencia: 1
      };

      service.agendarTransferencia(mockRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRequest);
      req.flush(mockResponse);
    });
  });

  describe('buscarTodasTransferencias', () => {
    it('should send GET request to fetch all transferências', () => {
      const mockResponse: TransferenciaResponse[] = [
        {
          id: 1,
          contaOrigem: '1234567890',
          contaDestino: '0987654321',
          valorTransferencia: 1000,
          taxaTransferencia: 12,
          dataTransferencia: '2025-07-22',
          dataAgendamento: '2025-07-21',
          diasParaTransferencia: 1
        }
      ];

      service.buscarTodasTransferencias().subscribe(response => {
        expect(response).toEqual(mockResponse);
        expect(response.length).toBe(1);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('buscarTransferenciaPorId', () => {
    it('should send GET request to fetch transferência by ID', () => {
      const mockResponse: TransferenciaResponse = {
        id: 1,
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        taxaTransferencia: 12,
        dataTransferencia: '2025-07-22',
        dataAgendamento: '2025-07-21',
        diasParaTransferencia: 1
      };

      service.buscarTransferenciaPorId(1).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${apiUrl}/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('buscarTransferenciasPorConta', () => {
    it('should send GET request to fetch transferências by conta', () => {
      const mockResponse: TransferenciaResponse[] = [
        {
          id: 1,
          contaOrigem: '1234567890',
          contaDestino: '0987654321',
          valorTransferencia: 1000,
          taxaTransferencia: 12,
          dataTransferencia: '2025-07-22',
          dataAgendamento: '2025-07-21',
          diasParaTransferencia: 1
        }
      ];

      service.buscarTransferenciasPorConta('1234567890').subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${apiUrl}/conta/1234567890`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('calcularTaxa', () => {
    it('should send GET request to calculate taxa', () => {
      const mockResponse: TaxaCalculoResponse = {
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22',
        taxaCalculada: 12,
        diasParaTransferencia: 1
      };

      service.calcularTaxa(1000, '2025-07-22').subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${apiUrl}/calcular-taxa?valor=1000&dataTransferencia=2025-07-22`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('verificarStatus', () => {
    it('should send GET request to check API status', () => {
      const mockResponse = {
        status: 'UP',
        service: 'Sistema de Transferências',
        timestamp: '2025-07-21T13:00:00Z'
      };

      service.verificarStatus().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${apiUrl}/health`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });
  });

  describe('Error handling', () => {
    it('should handle HTTP errors properly', () => {
      const mockRequest: TransferenciaRequest = {
        contaOrigem: '1234567890',
        contaDestino: '0987654321',
        valorTransferencia: 1000,
        dataTransferencia: '2025-07-22'
      };

      service.agendarTransferencia(mockRequest).subscribe({
        next: () => fail('should have failed with 400 error'),
        error: (error) => {
          expect(error.status).toBe(400);
        }
      });

      const req = httpMock.expectOne(apiUrl);
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });
  });
});

