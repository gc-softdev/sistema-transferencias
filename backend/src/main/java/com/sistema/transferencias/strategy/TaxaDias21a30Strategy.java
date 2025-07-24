package com.sistema.transferencias.strategy;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estratégia de cálculo de taxa para transferências de 21 a 30 dias.
 * Regra: aplicar taxa de 6,9% sobre o valor da transferência
 */

@Component
public class TaxaDias21a30Strategy implements TaxaCalculationStrategy {
    
    private static final BigDecimal TAXA_FIXA = BigDecimal.ZERO;
    private static final BigDecimal TAXA_PERCENTUAL = new BigDecimal("0.069"); // 6.9%
    private static final int DIAS_MIN = 21;
    private static final int DIAS_MAX = 30;
    
    @Override
    public BigDecimal calcularTaxa(BigDecimal valorTransferencia, int diasParaTransferencia) {
        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
        
        if (!isAplicavel(diasParaTransferencia)) {
            throw new IllegalArgumentException("Esta estratégia não é aplicável para " + diasParaTransferencia + " dias");
        }
        
        BigDecimal taxaPercentual = valorTransferencia.multiply(TAXA_PERCENTUAL);
        BigDecimal taxaTotal = TAXA_FIXA.add(taxaPercentual);
        
        return taxaTotal.setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public boolean isAplicavel(int diasParaTransferencia) {
        return diasParaTransferencia >= DIAS_MIN && diasParaTransferencia <= DIAS_MAX;
    }
    
    @Override
    public String getDescricao() {
        return "Taxa para transferência de 21 a 30 dias: R$ 0,00 + 6,9% sobre o valor";
    }
}
