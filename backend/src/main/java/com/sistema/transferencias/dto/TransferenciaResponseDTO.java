package com.sistema.transferencias.dto;

import com.sistema.transferencias.model.Transferencia;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferenciaResponseDTO {

    private Long id;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valorTransferencia;
    private BigDecimal taxaTransferencia;
    private LocalDate dataTransferencia;
    private LocalDate dataAgendamento;
    private Integer diasParaTransferencia;


    public TransferenciaResponseDTO() {}

    public TransferenciaResponseDTO(Transferencia transferencia) {
        this.id = transferencia.getId();
        this.contaOrigem = transferencia.getContaOrigem();
        this.contaDestino = transferencia.getContaDestino();
        this.valorTransferencia = transferencia.getValorTransferencia();
        this.taxaTransferencia = transferencia.getTaxaTransferencia();
        this.dataTransferencia = transferencia.getDataTransferencia();
        this.dataAgendamento = transferencia.getDataAgendamento();
        this.diasParaTransferencia = transferencia.getDiasParaTransferencia();
    }

    public TransferenciaResponseDTO(Long id, String contaOrigem, String contaDestino, 
                                   BigDecimal valorTransferencia, BigDecimal taxaTransferencia,
                                   LocalDate dataTransferencia, LocalDate dataAgendamento,
                                   Integer diasParaTransferencia) {
        this.id = id;
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valorTransferencia = valorTransferencia;
        this.taxaTransferencia = taxaTransferencia;
        this.dataTransferencia = dataTransferencia;
        this.dataAgendamento = dataAgendamento;
        this.diasParaTransferencia = diasParaTransferencia;
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
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public Integer getDiasParaTransferencia() {
        return diasParaTransferencia;
    }

    public void setDiasParaTransferencia(Integer diasParaTransferencia) {
        this.diasParaTransferencia = diasParaTransferencia;
    }

    @Override
    public String toString() {
        return "TransferenciaResponseDTO{" +
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
