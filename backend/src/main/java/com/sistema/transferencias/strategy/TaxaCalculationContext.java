package com.sistema.transferencias.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.List;

/**
 * Contexto do padrão Strategy para cálculo de taxa de transferência.
 * Esta classe é responsável por selecionar a estratégia apropriada
 * baseada no número de dias para transferência e delegar o cálculo.
 */

@Component
public class TaxaCalculationContext {
    
    private final List<TaxaCalculationStrategy> strategies;
    
    @Autowired
    public TaxaCalculationContext(List<TaxaCalculationStrategy> strategies) {
        this.strategies = strategies;
    }
    
    /**
     * Calcula a taxa de transferência usando a estratégia apropriada.
     * @throws IllegalArgumentException se não houver estratégia aplicável
     */

    public BigDecimal calcularTaxa(BigDecimal valorTransferencia, int diasParaTransferencia) {
        TaxaCalculationStrategy strategy = findStrategy(diasParaTransferencia);
        
        if (strategy == null) {
            throw new IllegalArgumentException(
                String.format("Não é possível calcular a taxa para transferência em %d dias. " +
                             "Transferências são permitidas apenas entre 0 e 50 dias.", 
                             diasParaTransferencia)
            );
        }
        
        return strategy.calcularTaxa(valorTransferencia, diasParaTransferencia);
    }
    
    /**
     * Encontra a estratégia aplicável para o número de dias informado.
     * @return Estratégia aplicável ou null se não encontrada
     */

    private TaxaCalculationStrategy findStrategy(int diasParaTransferencia) {
        return strategies.stream()
                .filter(strategy -> strategy.isAplicavel(diasParaTransferencia))
                .findFirst()
                .orElse(null);
    }
    

     // Verifica se existe uma estratégia aplicável para o número de dias.


    public boolean isTransferenciaPermitida(int diasParaTransferencia) {
        return findStrategy(diasParaTransferencia) != null;
    }
    
    /**
     * Retorna todas as estratégias disponíveis para fins de documentação.
     * @return Lista de descrições das estratégias
     */

    public List<String> getDescricoesEstrategias() {
        return strategies.stream()
                .map(TaxaCalculationStrategy::getDescricao)
                .toList();
    }
}
