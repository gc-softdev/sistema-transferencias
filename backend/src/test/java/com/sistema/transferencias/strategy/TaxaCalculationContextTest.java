package com.sistema.transferencias.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaxaCalculationContextTest {

    private TaxaCalculationStrategy strategyAplicavel;
    private TaxaCalculationStrategy strategyNaoAplicavel;
    private TaxaCalculationContext context;

    @BeforeEach
    void setUp() {
        strategyAplicavel = mock(TaxaCalculationStrategy.class);
        strategyNaoAplicavel = mock(TaxaCalculationStrategy.class);

        when(strategyAplicavel.isAplicavel(5)).thenReturn(true);
        when(strategyAplicavel.calcularTaxa(any(), eq(5))).thenReturn(new BigDecimal("10.00"));
        when(strategyAplicavel.getDescricao()).thenReturn("Taxa para 1 a 10 dias");

        when(strategyNaoAplicavel.isAplicavel(5)).thenReturn(false);
        when(strategyNaoAplicavel.getDescricao()).thenReturn("Outra estratégia");

        context = new TaxaCalculationContext(List.of(strategyNaoAplicavel, strategyAplicavel));
    }

    @Test
    void deveCalcularTaxaUsandoEstrategiaAplicavel() {
        BigDecimal valor = new BigDecimal("100.00");

        BigDecimal taxa = context.calcularTaxa(valor, 5);

        assertEquals(new BigDecimal("10.00"), taxa);
        verify(strategyAplicavel).calcularTaxa(valor, 5);
    }

    @Test
    void deveRetornarTrueParaTransferenciaPermitidaQuandoExisteEstrategia() {
        assertTrue(context.isTransferenciaPermitida(5));
    }

    @Test
    void deveRetornarFalseParaTransferenciaNaoPermitidaQuandoNaoHaEstrategia() {
        TaxaCalculationContext vazio = new TaxaCalculationContext(List.of());
        assertFalse(vazio.isTransferenciaPermitida(5));
    }

    @Test
    void deveLancarExcecaoQuandoNaoHaEstrategiaAplicavel() {
        TaxaCalculationContext semEstrategia = new TaxaCalculationContext(List.of(strategyNaoAplicavel));
        assertThrows(IllegalArgumentException.class,
                () -> semEstrategia.calcularTaxa(new BigDecimal("100"), 5));
    }

    @Test
    void deveRetornarDescricaoDasEstrategias() {
        List<String> descricoes = context.getDescricoesEstrategias();
        assertEquals(2, descricoes.size());
        assertTrue(descricoes.contains("Taxa para 1 a 10 dias"));
        assertTrue(descricoes.contains("Outra estratégia"));
    }
}
