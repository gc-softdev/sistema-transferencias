package com.sistema.transferencias.service;

import com.sistema.transferencias.dto.TransferenciaRequestDTO;
import com.sistema.transferencias.dto.TransferenciaResponseDTO;
import com.sistema.transferencias.exception.TaxaCalculationException;
import com.sistema.transferencias.model.Transferencia;
import com.sistema.transferencias.repository.TransferenciaRepository;
import com.sistema.transferencias.strategy.TaxaCalculationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferenciaServiceTest {

    private TransferenciaRepository transferenciaRepository;
    private TaxaCalculationContext taxaCalculationContext;
    private TransferenciaService transferenciaService;

    @BeforeEach
    void setUp() {
        transferenciaRepository = mock(TransferenciaRepository.class);
        taxaCalculationContext = mock(TaxaCalculationContext.class);
        transferenciaService = new TransferenciaService(transferenciaRepository, taxaCalculationContext);
    }

    @Test
    void agendarTransferenciaComSucesso() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setContaOrigem("123");
        dto.setContaDestino("456");
        dto.setValorTransferencia(new BigDecimal("20.00"));
        dto.setDataTransferencia(LocalDate.now().plusDays(5));

        when(taxaCalculationContext.isTransferenciaPermitida(5)).thenReturn(true);
        when(taxaCalculationContext.calcularTaxa(any(), eq(5))).thenReturn(new BigDecimal("5.00"));
        when(transferenciaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransferenciaResponseDTO response = transferenciaService.agendarTransferencia(dto);

        assertEquals(dto.getContaOrigem(), response.getContaOrigem());
        verify(transferenciaRepository, times(1)).save(any());
    }

    @Test
    void agendarTransferenciaComContasIguaisDeveLancarExcecao() {
        TransferenciaRequestDTO dto = new TransferenciaRequestDTO();
        dto.setContaOrigem("123");
        dto.setContaDestino("123");
        dto.setValorTransferencia(BigDecimal.TEN);
        dto.setDataTransferencia(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> transferenciaService.agendarTransferencia(dto));
    }

    @Test
    void calcularTaxaTransferenciaComDadosValidos() {
        BigDecimal valor = new BigDecimal("15.00");
        LocalDate data = LocalDate.now().plusDays(1);

        when(taxaCalculationContext.isTransferenciaPermitida(1)).thenReturn(true);
        when(taxaCalculationContext.calcularTaxa(valor, 1)).thenReturn(new BigDecimal("3.00"));

        BigDecimal taxa = transferenciaService.calcularTaxaTransferencia(valor, data);

        assertEquals(new BigDecimal("3.00"), taxa);
    }

    @Test
    void calcularTaxaTransferenciaComDataInvalidaDeveLancarExcecao() {
        assertThrows(TaxaCalculationException.class, () ->
                transferenciaService.calcularTaxaTransferencia(BigDecimal.TEN, LocalDate.now().minusDays(1))
        );
    }

    @Test
    void buscarTransferenciaPorIdComSucesso() {
        Transferencia t = new Transferencia();
        t.setId(1L);
        t.setContaOrigem("123");
        t.setContaDestino("456");

        when(transferenciaRepository.findById(1L)).thenReturn(Optional.of(t));

        TransferenciaResponseDTO response = transferenciaService.buscarTransferenciaPorId(1L);

        assertEquals("123", response.getContaOrigem());
    }

    @Test
    void buscarTransferenciaPorIdInexistenteDeveLancarExcecao() {
        when(transferenciaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> transferenciaService.buscarTransferenciaPorId(999L));
    }

    @Test
    void buscarTodasTransferenciasDeveRetornarLista() {
        when(transferenciaRepository.findAllByOrderByDataAgendamentoDesc()).thenReturn(Collections.emptyList());

        assertTrue(transferenciaService.buscarTodasTransferencias().isEmpty());
    }

    @Test
    void buscarPorContaOrigemDeveRetornarLista() {
        when(transferenciaRepository.findByContaOrigemOrderByDataAgendamentoDesc("123"))
                .thenReturn(Collections.emptyList());

        assertTrue(transferenciaService.buscarTransferenciasPorContaOrigem("123").isEmpty());
    }
}
