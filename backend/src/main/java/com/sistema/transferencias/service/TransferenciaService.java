package com.sistema.transferencias.service;

import com.sistema.transferencias.dto.TransferenciaRequestDTO;
import com.sistema.transferencias.dto.TransferenciaResponseDTO;
import com.sistema.transferencias.exception.TaxaCalculationException;
import com.sistema.transferencias.model.Transferencia;
import com.sistema.transferencias.repository.TransferenciaRepository;
import com.sistema.transferencias.strategy.TaxaCalculationContext;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pela lógica de negócio das transferências.
 * Implementa as regras de negócio, validações e coordena
 * o cálculo de taxas através do padrão Strategy.
 */

@Service
public class TransferenciaService {

    private static final BigDecimal VALOR_MINIMO_DIA_ZERO = new BigDecimal("3.00");
    private static final BigDecimal VALOR_MINIMO_DIAS_1_A_10 = new BigDecimal("12.00");

    private final TransferenciaRepository transferenciaRepository;
    private final TaxaCalculationContext taxaCalculationContext;

    public TransferenciaService(TransferenciaRepository transferenciaRepository, TaxaCalculationContext taxaCalculationContext) {
        this.transferenciaRepository = transferenciaRepository;
        this.taxaCalculationContext = taxaCalculationContext;
    }

    public TransferenciaResponseDTO agendarTransferencia(TransferenciaRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Dados da transferência são obrigatórios");
        }
        if (requestDTO.getContaOrigem().equals(requestDTO.getContaDestino())) {
            throw new IllegalArgumentException("Conta de origem e destino não podem ser iguais");
        }
        if (requestDTO.getValorTransferencia() == null || requestDTO.getValorTransferencia().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
        if (requestDTO.getDataTransferencia() == null || requestDTO.getDataTransferencia().isBefore(LocalDate.now())) {
            throw new TaxaCalculationException("Data da transferência deve ser igual ou posterior à data atual");
        }

        long dias = ChronoUnit.DAYS.between(LocalDate.now(), requestDTO.getDataTransferencia());


        if (dias == 0 && requestDTO.getValorTransferencia().compareTo(VALOR_MINIMO_DIA_ZERO) < 0) {
            throw new TaxaCalculationException(
                    String.format("Valor da transferência deve ser maior ou igual a R$ %s para transferências no mesmo dia",
                            VALOR_MINIMO_DIA_ZERO.setScale(2, RoundingMode.HALF_UP))
            );
        }
        if (dias >= 1 && dias <= 10 && requestDTO.getValorTransferencia().compareTo(VALOR_MINIMO_DIAS_1_A_10) < 0) {
            throw new TaxaCalculationException(
                    String.format("Valor da transferência deve ser maior ou igual a R$ %s para transferências de 1 a 10 dias",
                            VALOR_MINIMO_DIAS_1_A_10.setScale(2, RoundingMode.HALF_UP))
            );
        }

        if (!taxaCalculationContext.isTransferenciaPermitida((int) dias)) {
            throw new TaxaCalculationException(
                    String.format("Transferência não permitida para %d dias. Transferências são permitidas apenas entre 0 e 50 dias.", dias)
            );
        }

        BigDecimal taxa = taxaCalculationContext.calcularTaxa(requestDTO.getValorTransferencia(), (int) dias);

        Transferencia transferencia = new Transferencia();
        transferencia.setContaOrigem(requestDTO.getContaOrigem());
        transferencia.setContaDestino(requestDTO.getContaDestino());
        transferencia.setValorTransferencia(requestDTO.getValorTransferencia());
        transferencia.setDataTransferencia(requestDTO.getDataTransferencia());
        transferencia.setDataAgendamento(LocalDate.now());
        transferencia.setTaxaTransferencia(taxa);

        Transferencia savedTransferencia = transferenciaRepository.save(transferencia);
        return new TransferenciaResponseDTO(savedTransferencia);
    }

    public BigDecimal calcularTaxaTransferencia(BigDecimal valorTransferencia, LocalDate dataTransferencia) {
        if (valorTransferencia == null || valorTransferencia.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser maior que zero");
        }
        if (dataTransferencia == null || dataTransferencia.isBefore(LocalDate.now())) {
            throw new TaxaCalculationException("Data da transferência deve ser igual ou posterior à data atual");
        }

        long dias = ChronoUnit.DAYS.between(LocalDate.now(), dataTransferencia);

        if (dias == 0 && valorTransferencia.compareTo(VALOR_MINIMO_DIA_ZERO) < 0) {
            throw new TaxaCalculationException(
                    String.format("Valor da transferência deve ser maior ou igual a R$ %s para transferências no mesmo dia",
                            VALOR_MINIMO_DIA_ZERO.setScale(2, RoundingMode.HALF_UP))
            );
        }
        if (dias >= 1 && dias <= 10 && valorTransferencia.compareTo(VALOR_MINIMO_DIAS_1_A_10) < 0) {
            throw new TaxaCalculationException(
                    String.format("Valor da transferência deve ser maior ou igual a R$ %s para transferências de 1 a 10 dias",
                            VALOR_MINIMO_DIAS_1_A_10.setScale(2, RoundingMode.HALF_UP))
            );
        }

        if (!taxaCalculationContext.isTransferenciaPermitida((int) dias)) {
            throw new TaxaCalculationException(
                    String.format("Transferência não permitida para %d dias. Transferências são permitidas apenas entre 0 e 50 dias.", dias)
            );
        }

        return taxaCalculationContext.calcularTaxa(valorTransferencia, (int) dias);
    }

    public List<TransferenciaResponseDTO> buscarTodasTransferencias() {
        return transferenciaRepository.findAllByOrderByDataAgendamentoDesc()
                .stream()
                .map(TransferenciaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public TransferenciaResponseDTO buscarTransferenciaPorId(Long id) {
        Transferencia transferencia = transferenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transferência não encontrada com ID: " + id));
        return new TransferenciaResponseDTO(transferencia);
    }

    public List<TransferenciaResponseDTO> buscarTransferenciasPorContaOrigem(String contaOrigem) {
        return transferenciaRepository.findByContaOrigemOrderByDataAgendamentoDesc(contaOrigem)
                .stream()
                .map(TransferenciaResponseDTO::new)
                .collect(Collectors.toList());
    }
}
