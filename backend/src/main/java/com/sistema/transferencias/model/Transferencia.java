package com.sistema.transferencias.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transferencias")
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Conta de origem é obrigatória")
    @Pattern(regexp = "\\d{10}", message = "Conta de origem deve ter exatamente 10 dígitos")
    @Column(name = "conta_origem", nullable = false, length = 10)
    private String contaOrigem;

    @NotBlank(message = "Conta de destino é obrigatória")
    @Pattern(regexp = "\\d{10}", message = "Conta de destino deve ter exatamente 10 dígitos")
    @Column(name = "conta_destino", nullable = false, length = 10)
    private String contaDestino;

    @NotNull(message = "Valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor da transferência deve ser maior que zero")
    @Column(name = "valor_transferencia", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTransferencia;

    @NotNull(message = "Taxa de transferência é obrigatória")
    @DecimalMin(value = "0.00", message = "Taxa de transferência não pode ser negativa")
    @Column(name = "taxa_transferencia", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxaTransferencia;

    @NotNull(message = "Data da transferência é obrigatória")
    @FutureOrPresent(message = "Data da transferência deve ser igual ou posterior à data atual")
    @Column(name = "data_transferencia", nullable = false)
    private LocalDate dataTransferencia;

    @NotNull(message = "Data de agendamento é obrigatória")
    @Column(name = "data_agendamento", nullable = false)
    private LocalDate dataAgendamento;

    @Column(name = "dias_para_transferencia", nullable = false)
    private Integer diasParaTransferencia;


    public Transferencia() {
        this.dataAgendamento = LocalDate.now();
    }

    public Transferencia(String contaOrigem, String contaDestino, BigDecimal valorTransferencia, 
                        LocalDate dataTransferencia) {
        this();
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valorTransferencia = valorTransferencia;
        this.dataTransferencia = dataTransferencia;
        this.diasParaTransferencia = calcularDiasParaTransferencia();
    }


    private Integer calcularDiasParaTransferencia() {
        if (dataAgendamento != null && dataTransferencia != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(dataAgendamento, dataTransferencia);
        }
        return 0;
    }

    @PrePersist
    @PreUpdate
    private void atualizarDiasParaTransferencia() {
        this.diasParaTransferencia = calcularDiasParaTransferencia();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(String contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    public String getContaDestino() {
        return contaDestino;
    }

    public void setContaDestino(String contaDestino) {
        this.contaDestino = contaDestino;
    }

    public BigDecimal getValorTransferencia() {
        return valorTransferencia;
    }

    public void setValorTransferencia(BigDecimal valorTransferencia) {
        this.valorTransferencia = valorTransferencia;
    }

    public BigDecimal getTaxaTransferencia() {
        return taxaTransferencia;
    }

    public void setTaxaTransferencia(BigDecimal taxaTransferencia) {
        this.taxaTransferencia = taxaTransferencia;
    }

    public LocalDate getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(LocalDate dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
        this.diasParaTransferencia = calcularDiasParaTransferencia();
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
        this.diasParaTransferencia = calcularDiasParaTransferencia();
    }

    public Integer getDiasParaTransferencia() {
        return diasParaTransferencia;
    }

    public void setDiasParaTransferencia(Integer diasParaTransferencia) {
        this.diasParaTransferencia = diasParaTransferencia;
    }

    @Override
    public String toString() {
        return "Transferencia{" +
                "id=" + id +
                ", contaOrigem='" + contaOrigem + '\'' +
                ", contaDestino='" + contaDestino + '\'' +
                ", valorTransferencia=" + valorTransferencia +
                ", taxaTransferencia=" + taxaTransferencia +
                ", dataTransferencia=" + dataTransferencia +
                ", dataAgendamento=" + dataAgendamento +
                ", diasParaTransferencia=" + diasParaTransferencia +
                '}';
    }
}

