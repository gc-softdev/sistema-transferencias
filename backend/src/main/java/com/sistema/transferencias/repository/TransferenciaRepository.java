package com.sistema.transferencias.repository;

import com.sistema.transferencias.model.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository para operações de persistência da entidade Transferencia.
 * Utiliza Spring Data JPA para fornecer operações CRUD básicas
 * e consultas customizadas.
 */

@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    
    /**
     * Busca todas as transferências ordenadas por data de agendamento (mais recente primeiro).
     * @return Lista de transferências ordenadas
     */

    List<Transferencia> findAllByOrderByDataAgendamentoDesc();
    
    /**
     * Busca transferências por conta de origem.
     * @param contaOrigem Conta de origem
     * @return Lista de transferências da conta
     */

    List<Transferencia> findByContaOrigemOrderByDataAgendamentoDesc(String contaOrigem);
    
    /**
     * Busca transferências por data de transferência.
     * @param dataTransferencia Data da transferência
     * @return Lista de transferências na data
     */

    List<Transferencia> findByDataTransferenciaOrderByDataAgendamentoDesc(LocalDate dataTransferencia);
    
    /**
     * Busca transferências agendadas entre duas datas.
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Lista de transferências no período
     */

    @Query("SELECT t FROM Transferencia t WHERE t.dataAgendamento BETWEEN :dataInicio AND :dataFim ORDER BY t.dataAgendamento DESC")
    List<Transferencia> findByDataAgendamentoBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Busca transferências com data de transferência entre duas datas.
     * @param dataInicio Data de início
     * @param dataFim Data de fim
     * @return Lista de transferências no período
     */

    @Query("SELECT t FROM Transferencia t WHERE t.dataTransferencia BETWEEN :dataInicio AND :dataFim ORDER BY t.dataTransferencia ASC")
    List<Transferencia> findByDataTransferenciaBetween(LocalDate dataInicio, LocalDate dataFim);
    
    /**
     * Conta o número de transferências por conta de origem.
     * @param contaOrigem Conta de origem
     * @return Número de transferências
     */

    long countByContaOrigem(String contaOrigem);
}

