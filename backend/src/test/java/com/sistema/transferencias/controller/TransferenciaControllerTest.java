package com.sistema.transferencias.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.transferencias.dto.TransferenciaRequestDTO;
import com.sistema.transferencias.dto.TransferenciaResponseDTO;
import com.sistema.transferencias.model.Transferencia;
import com.sistema.transferencias.service.TransferenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferenciaController.class)
public class TransferenciaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferenciaService transferenciaService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransferenciaRequestDTO requestDTO;
    private TransferenciaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new TransferenciaRequestDTO();
        requestDTO.setContaOrigem("1234567890");
        requestDTO.setContaDestino("0987654321");
        requestDTO.setValorTransferencia(new BigDecimal("100.00"));
        requestDTO.setDataTransferencia(LocalDate.now().plusDays(1));

        Transferencia transferencia = new Transferencia();
        transferencia.setContaOrigem("1234567890");
        transferencia.setContaDestino("0987654321");
        transferencia.setValorTransferencia(new BigDecimal("100.00"));
        transferencia.setDataTransferencia(requestDTO.getDataTransferencia());
        transferencia.setTaxaTransferencia(new BigDecimal("5.00"));
        transferencia.setDataAgendamento(LocalDate.now());

        responseDTO = new TransferenciaResponseDTO(transferencia);
    }

    @Test
    void testAgendarTransferencia() throws Exception {
        Mockito.when(transferenciaService.agendarTransferencia(any())).thenReturn(responseDTO);

        mockMvc.perform(post("/api/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contaOrigem").value("1234567890"));
    }

    @Test
    void testBuscarTodasTransferencias() throws Exception {
        Mockito.when(transferenciaService.buscarTodasTransferencias()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/transferencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contaOrigem").value("1234567890"));
    }

    @Test
    void testBuscarTransferenciaPorId() throws Exception {
        Mockito.when(transferenciaService.buscarTransferenciaPorId(1L)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/transferencias/1"))
                .andExpect(status().isOk())
                // CORREÇÃO AQUI: O valor esperado deve ser "1234567890"
                .andExpect(jsonPath("$.contaOrigem").value("1234567890"));
    }

    @Test
    void testBuscarTransferenciasPorConta() throws Exception {
        Mockito.when(transferenciaService.buscarTransferenciasPorContaOrigem("1234567890"))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/transferencias/conta/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contaOrigem").value("1234567890"));
    }

    @Test
    void testCalcularTaxa() throws Exception {
        Mockito.when(transferenciaService.calcularTaxaTransferencia(any(), any()))
                .thenReturn(new BigDecimal("5.00"));

        mockMvc.perform(get("/api/transferencias/calcular-taxa")
                        .param("valor", "100.00")
                        .param("dataTransferencia", LocalDate.now().plusDays(1).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taxaCalculada").value(5.0));
    }

    @Test
    void testHealthCheck() throws Exception {
        mockMvc.perform(get("/api/transferencias/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}