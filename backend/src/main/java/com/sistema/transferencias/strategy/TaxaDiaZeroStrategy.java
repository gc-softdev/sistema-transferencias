package com.sistema.transferencias.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia de cálculo de taxa para transferências no mesmo dia (dia 0).
 * Adotei a regra de transferência mínima: R$ 3,00 + 2,5% sobre o valor da transferência.
 */


@Component
public class TaxaDiaZeroStrategy implements TaxaCalculationStrategy {

    private static final BigDecimal VALOR_MINIMO = new BigDecimal("3.00");
    private static final BigDecimal TAXA_PERCENTUAL = new BigDecimal("0.025"); // 2.5%
    private static final int DIAS_APLICAVEL = 0;

    @Override
    public BigDecimal calcularTaxa(BigDecimal valorTransferencia, int diasParaTransferencia) {
        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }

        if (valorTransferencia.compareTo(VALOR_MINIMO) < 0) {
            throw new IllegalArgumentException(
                    String.format("Valor da transferência deve ser maior ou igual a R$ %s para transferências no mesmo dia",
                            VALOR_MINIMO.setScale(2, RoundingMode.HALF_UP))
            );
        }

        if (!isAplicavel(diasParaTransferencia)) {
            throw new IllegalArgumentException("Esta estratégia não é aplicável para " + diasParaTransferencia + " dias");
        }

        // Calcula apenas a taxa percentual (2,5%)
        BigDecimal taxaPercentual = valorTransferencia.multiply(TAXA_PERCENTUAL);
        return taxaPercentual.setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isAplicavel(int diasParaTransferencia) {
        return diasParaTransferencia == DIAS_APLICAVEL;
    }

    @Override
    public String getDescricao() {
        return String.format("Taxa para transferência no mesmo dia: 2,5%% sobre o valor (mínimo R$ %s)",
                VALOR_MINIMO.setScale(2, RoundingMode.HALF_UP));
    }
}
