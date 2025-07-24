package com.sistema.transferencias.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TaxaDiaZeroStrategyTest {

    private TaxaDiaZeroStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TaxaDiaZeroStrategy();
    }

    @Test
    void deveCalcularTaxaCorretamenteParaValorValido() {
        BigDecimal valor = new BigDecimal("100.00");
        BigDecimal taxa = strategy.calcularTaxa(valor, 0);
        assertEquals(new BigDecimal("2.50"), taxa, "A taxa deve ser 2,5% do valor (R$ 2,50 para R$ 100,00)");
    }

    @Test
    void deveLancarExcecaoParaValorMenorQueMinimo() {
        BigDecimal valor = new BigDecimal("2.99");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 0));
        assertEquals("Valor da transferência deve ser maior ou igual a R$ 3.00 para transferências no mesmo dia",
                exception.getMessage());
    }

    @Test
    void deveLancarExcecaoParaValorNulo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(null, 0));
        assertEquals("Valor da transferência deve ser maior que zero", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoParaValorZero() {
        BigDecimal valor = BigDecimal.ZERO;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 0));
        assertEquals("Valor da transferência deve ser maior que zero", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoParaDiasDiferentesDeZero() {
        BigDecimal valor = new BigDecimal("100.00");
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 1));
        assertEquals("Esta estratégia não é aplicável para 1 dias", exception.getMessage());
    }

    @Test
    void isAplicavelDeveRetornarTrueParaZeroDias() {
        assertTrue(strategy.isAplicavel(0), "A estratégia deve ser aplicável para 0 dias");
    }

    @Test
    void isAplicavelDeveRetornarFalseParaDiasDiferentesDeZero() {
        assertFalse(strategy.isAplicavel(1), "A estratégia não deve ser aplicável para 1 dia");
        assertFalse(strategy.isAplicavel(-1), "A estratégia não deve ser aplicável para -1 dia");
    }

    @Test
    void deveRetornarDescricaoCorreta() {
        String descricao = strategy.getDescricao();
        assertEquals("Taxa para transferência no mesmo dia: 2,5% sobre o valor (mínimo R$ 3.00)", descricao,
                "A descrição deve corresponder ao formato esperado com mínimo R$ 3.00");
    }
}