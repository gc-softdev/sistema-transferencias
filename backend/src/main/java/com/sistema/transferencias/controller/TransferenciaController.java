package com.sistema.transferencias.controller;

import com.sistema.transferencias.dto.TransferenciaRequestDTO;
import com.sistema.transferencias.dto.TransferenciaResponseDTO;
import com.sistema.transferencias.service.TransferenciaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para operações relacionadas a transferências.
 * Fornece endpoints para agendamento, consulta e cálculo de taxas
 * de transferências financeiras.
 */

@RestController
@RequestMapping("/api/transferencias")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TransferenciaController {
    
    private final TransferenciaService transferenciaService;
    
    @Autowired
    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }
    
    /**
     * Agenda uma nova transferência.
     * @param requestDTO Dados da transferência
     * @return Transferência agendada com taxa calculada
     */

    @PostMapping
    public ResponseEntity<TransferenciaResponseDTO> agendarTransferencia(
            @Valid @RequestBody TransferenciaRequestDTO requestDTO) {
        
        TransferenciaResponseDTO response = transferenciaService.agendarTransferencia(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Busca todas as transferências agendadas.
     * @return Lista de todas as transferências
     */

    @GetMapping
    public ResponseEntity<List<TransferenciaResponseDTO>> buscarTodasTransferencias() {
        List<TransferenciaResponseDTO> transferencias = transferenciaService.buscarTodasTransferencias();
        return ResponseEntity.ok(transferencias);
    }
    
    /**
     * Busca uma transferência específica por ID.
     * @param id ID da transferência
     * @return Transferência encontrada
     */

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaResponseDTO> buscarTransferenciaPorId(@PathVariable Long id) {
        TransferenciaResponseDTO transferencia = transferenciaService.buscarTransferenciaPorId(id);
        return ResponseEntity.ok(transferencia);
    }
    
    /**
     * Busca transferências por conta de origem.
     * @param contaOrigem Conta de origem
     * @return Lista de transferências da conta
     */

    @GetMapping("/conta/{contaOrigem}")
    public ResponseEntity<List<TransferenciaResponseDTO>> buscarTransferenciasPorConta(
            @PathVariable String contaOrigem) {
        
        List<TransferenciaResponseDTO> transferencias = 
                transferenciaService.buscarTransferenciasPorContaOrigem(contaOrigem);
        return ResponseEntity.ok(transferencias);
    }
    
    /**
     * Calcula a taxa para uma transferência sem agendá-la.
     * @param valor Valor da transferência
     * @param dataTransferencia Data da transferência (formato: YYYY-MM-DD)
     * @return Taxa calculada
     */

    @GetMapping("/calcular-taxa")
    public ResponseEntity<Map<String, Object>> calcularTaxa(
            @RequestParam BigDecimal valor,
            @RequestParam String dataTransferencia) {
        
        LocalDate data = LocalDate.parse(dataTransferencia);
        BigDecimal taxa = transferenciaService.calcularTaxaTransferencia(valor, data);
        
        Map<String, Object> response = Map.of(
            "valorTransferencia", valor,
            "dataTransferencia", data,
            "taxaCalculada", taxa,
            "diasParaTransferencia", java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), data)
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint de health check.
     * @return Status da aplicação
     */

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = Map.of(
            "status", "UP",
            "timestamp", java.time.LocalDateTime.now().toString(),
            "service", "Sistema de Transferências"
        );
        return ResponseEntity.ok(response);
    }
}
