package com.sistema.transferencias.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransferenciaRequestDTO {

    @NotBlank(message = "Conta de origem é obrigatória")
    @Pattern(regexp = "\\d{10}", message = "Conta de origem deve ter exatamente 10 dígitos")
    private String contaOrigem;

    @NotBlank(message = "Conta de destino é obrigatória")
    @Pattern(regexp = "\\d{10}", message = "Conta de destino deve ter exatamente 10 dígitos")
    private String contaDestino;

    @NotNull(message = "Valor da transferência é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor da transferência deve ser maior que zero")
    private BigDecimal valorTransferencia;

    @NotNull(message = "Data da transferência é obrigatória")
    @FutureOrPresent(message = "Data da transferência deve ser igual ou posterior à data atual")
    private LocalDate dataTransferencia;


    public TransferenciaRequestDTO() {}

    public TransferenciaRequestDTO(String contaOrigem, String contaDestino, 
                                  BigDecimal valorTransferencia, LocalDate dataTransferencia) {
        this.contaOrigem = contaOrigem;
        this.contaDestino = contaDestino;
        this.valorTransferencia = valorTransferencia;
        this.dataTransferencia = dataTransferencia;
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

    public LocalDate getDataTransferencia() {
        return dataTransferencia;
    }

    public void setDataTransferencia(LocalDate dataTransferencia) {
        this.dataTransferencia = dataTransferencia;
    }

    @Override
    public String toString() {
        return "TransferenciaRequestDTO{" +
                "contaOrigem='" + contaOrigem + '\'' +
                ", contaDestino='" + contaDestino + '\'' +
                ", valorTransferencia=" + valorTransferencia +
                ", dataTransferencia=" + dataTransferencia +
                '}';
    }
}
