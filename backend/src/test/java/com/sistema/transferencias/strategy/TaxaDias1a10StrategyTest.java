package com.sistema.transferencias.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TaxaDias1a10StrategyTest {

    private TaxaDias1a10Strategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new TaxaDias1a10Strategy();
    }

    @Test
    void deveLancarExcecaoSeValorTransferenciaForMenorQueMinimo() {
        BigDecimal valor = new BigDecimal("11.99");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 5));

        assertTrue(ex.getMessage().contains("Valor da transferência deve ser maior ou igual a R$"));
    }

    @Test
    void deveLancarExcecaoSeValorForNulo() {
        assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(null, 3));
    }

    @Test
    void deveLancarExcecaoSeDiasForaDoIntervalo() {
        BigDecimal valor = new BigDecimal("20.00");

        assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 0));

        assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 11));
    }

    @Test
    void deveLancarExcecaoMesmoComValorValidoDentroDoIntervaloPermitido() {
        BigDecimal valor = new BigDecimal("20.00");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                strategy.calcularTaxa(valor, 5));

        assertEquals("Transferência negada, não há taxa percentual aplicável para esta data", ex.getMessage());
    }

    @Test
    void isAplicavelDeveRetornarTrueParaDiasDe1a10() {
        for (int i = 1; i <= 10; i++) {
            assertTrue(strategy.isAplicavel(i), "Esperado true para dia " + i);
        }
    }

    @Test
    void isAplicavelDeveRetornarFalseForaDoIntervalo() {
        assertFalse(strategy.isAplicavel(0));
        assertFalse(strategy.isAplicavel(11));
    }

    @Test
    void deveRetornarDescricaoCorreta() {
        String descricao = strategy.getDescricao();

        assertTrue(descricao.contains("Transferências de 1 a 10 dias não são permitidas"));
        assertTrue(descricao.contains("R$ 12,00"));
    }
}
