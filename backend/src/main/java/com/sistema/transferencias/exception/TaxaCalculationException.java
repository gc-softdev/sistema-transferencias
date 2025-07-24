package com.sistema.transferencias.exception;

/**
 * Exceção customizada para erros no cálculo de taxa de transferência.
 * Esta exceção é lançada quando não é possível calcular a taxa
 * para uma transferência devido a parâmetros inválidos ou
 * falta de estratégia aplicável.
 */

public class TaxaCalculationException extends RuntimeException {
    
    public TaxaCalculationException(String message) {
        super(message);
    }
    
    public TaxaCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public TaxaCalculationException(Throwable cause) {
        super(cause);
    }
}

