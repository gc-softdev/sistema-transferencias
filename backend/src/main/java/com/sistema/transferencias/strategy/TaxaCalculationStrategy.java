package com.sistema.transferencias.strategy;

import java.math.BigDecimal;

/**
 * Interface Strategy para cálculo de taxa de transferência.
 * Esta interface define o contrato para diferentes estratégias de cálculo de taxa
 * baseadas no número de dias entre o agendamento e a transferência.
 * Implementa o padrão Strategy para permitir diferentes algoritmos de cálculo
 * sem modificar o código cliente.
 */

public interface TaxaCalculationStrategy {
    
    /**
     * Calcula a taxa de transferência baseada no valor e número de dias.
     * @throws IllegalArgumentException se os parâmetros forem inválidos
     */

    BigDecimal calcularTaxa(BigDecimal valorTransferencia, int diasParaTransferencia);
    
    /**
     * Verifica se esta estratégia é aplicável para o número de dias informado.
     * @return true se a estratégia é aplicável, false caso contrário
     */

    boolean isAplicavel(int diasParaTransferencia);


    String getDescricao();
}

