package com.sistema.transferencias.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Estratégia de cálculo de taxa para transferências de 1 a 10 dias.
 * Adotei a regra de transferência mínima: R$ 12,00 + 0,0% sobre o valor da transferência
 */

@Component
public class TaxaDias1a10Strategy implements TaxaCalculationStrategy {

    private static final BigDecimal VALOR_MINIMO = new BigDecimal("12.00");
    private static final BigDecimal TAXA_PERCENTUAL = BigDecimal.ZERO; // 0.0%
    private static final int DIAS_MIN = 1;
    private static final int DIAS_MAX = 10;
    private final DecimalFormat decimalFormat;

    public TaxaDias1a10Strategy() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        decimalFormat = new DecimalFormat("#,##0.00", symbols);
    }

    @Override
    public BigDecimal calcularTaxa(BigDecimal valorTransferencia, int diasParaTransferencia) {
        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }

        if (valorTransferencia.compareTo(VALOR_MINIMO) < 0) {
            throw new IllegalArgumentException(
                    String.format("Valor da transferência deve ser maior ou igual a R$ %s para transferências de 1 a 10 dias",
                            decimalFormat.format(VALOR_MINIMO))
            );
        }

        if (!isAplicavel(diasParaTransferencia)) {
            throw new IllegalArgumentException("Esta estratégia não é aplicável para " + diasParaTransferencia + " dias");
        }

        // Bloqueia a transferência devido à taxa percentual de 0,0%
        throw new IllegalArgumentException("Transferência negada, não há taxa percentual aplicável para esta data");
    }

    @Override
    public boolean isAplicavel(int diasParaTransferencia) {
        return diasParaTransferencia >= DIAS_MIN && diasParaTransferencia <= DIAS_MAX;
    }

    @Override
    public String getDescricao() {
        return String.format("Transferências de 1 a 10 dias não são permitidas (valor mínimo R$ %s, sem taxa percentual aplicável)",
                decimalFormat.format(VALOR_MINIMO));
    }
}
