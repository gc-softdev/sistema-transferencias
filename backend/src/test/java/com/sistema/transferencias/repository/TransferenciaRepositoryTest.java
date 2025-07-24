package com.sistema.transferencias.repository;

import com.sistema.transferencias.model.Transferencia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Testes para TransferenciaRepository")
class TransferenciaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    private Transferencia transferencia1;
    private Transferencia transferencia2;
    private Transferencia transferencia3;

    @BeforeEach
    void setUp() {
        transferencia1 = new Transferencia();
        transferencia1.setContaOrigem("1111111111");
        transferencia1.setContaDestino("2222222222");
        transferencia1.setValorTransferencia(new BigDecimal("1000.00"));
        transferencia1.setTaxaTransferencia(new BigDecimal("12.00"));
        transferencia1.setDataTransferencia(LocalDate.now().plusDays(5));
        transferencia1.setDataAgendamento(LocalDate.now());

        transferencia2 = new Transferencia();
        transferencia2.setContaOrigem("1111111111");
        transferencia2.setContaDestino("3333333333");
        transferencia2.setValorTransferencia(new BigDecimal("500.00"));
        transferencia2.setTaxaTransferencia(new BigDecimal("6.00"));
        transferencia2.setDataTransferencia(LocalDate.now().plusDays(10));
        transferencia2.setDataAgendamento(LocalDate.now().minusDays(1));

        transferencia3 = new Transferencia();
        transferencia3.setContaOrigem("4444444444");
        transferencia3.setContaDestino("5555555555");
        transferencia3.setValorTransferencia(new BigDecimal("2000.00"));
        transferencia3.setTaxaTransferencia(new BigDecimal("24.00"));
        transferencia3.setDataTransferencia(LocalDate.now().plusDays(15));
        transferencia3.setDataAgendamento(LocalDate.now().minusDays(2));

        entityManager.persistAndFlush(transferencia1);
        entityManager.persistAndFlush(transferencia2);
        entityManager.persistAndFlush(transferencia3);
    }

    @Test
    @DisplayName("Deve buscar todas as transferências ordenadas por data de agendamento desc")
    void deveBuscarTodasTransferenciasOrdenadasPorDataAgendamentoDesc() {
        // When
        List<Transferencia> transferencias = transferenciaRepository.findAllByOrderByDataAgendamentoDesc();

        // Then
        assertEquals(3, transferencias.size());
        // Primeira deve ser a mais recente (transferencia1 - hoje)
        assertEquals(transferencia1.getId(), transferencias.get(0).getId());
        // Segunda deve ser transferencia2 (ontem)
        assertEquals(transferencia2.getId(), transferencias.get(1).getId());
        // Terceira deve ser transferencia3 (anteontem)
        assertEquals(transferencia3.getId(), transferencias.get(2).getId());
    }

    @Test
    @DisplayName("Deve buscar transferências por conta de origem")
    void deveBuscarTransferenciasPorContaOrigem() {

        List<Transferencia> transferencias = transferenciaRepository
                .findByContaOrigemOrderByDataAgendamentoDesc("1111111111");


        assertEquals(2, transferencias.size());
        assertEquals("1111111111", transferencias.get(0).getContaOrigem());
        assertEquals("1111111111", transferencias.get(1).getContaOrigem());

        assertTrue(transferencias.get(0).getDataAgendamento()
                .isAfter(transferencias.get(1).getDataAgendamento()) ||
                transferencias.get(0).getDataAgendamento()
                .isEqual(transferencias.get(1).getDataAgendamento()));
    }

    @Test
    @DisplayName("Deve buscar transferências por data de transferência")
    void deveBuscarTransferenciasPorDataTransferencia() {

        List<Transferencia> transferencias = transferenciaRepository
                .findByDataTransferenciaOrderByDataAgendamentoDesc(LocalDate.now().plusDays(5));


        assertEquals(1, transferencias.size());
        assertEquals(transferencia1.getId(), transferencias.get(0).getId());
    }

    @Test
    @DisplayName("Deve buscar transferências por período de agendamento")
    void deveBuscarTransferenciasPorPeriodoAgendamento() {

        LocalDate dataInicio = LocalDate.now().minusDays(3);
        LocalDate dataFim = LocalDate.now();

        List<Transferencia> transferencias = transferenciaRepository
                .findByDataAgendamentoBetween(dataInicio, dataFim);

        assertEquals(3, transferencias.size());
        // Verifica se todas estão no período
        for (Transferencia t : transferencias) {
            assertTrue(t.getDataAgendamento().isAfter(dataInicio.minusDays(1)) &&
                      t.getDataAgendamento().isBefore(dataFim.plusDays(1)));
        }
    }

    @Test
    @DisplayName("Deve buscar transferências por período de transferência")
    void deveBuscarTransferenciasPorPeriodoTransferencia() {

        LocalDate dataInicio = LocalDate.now().plusDays(4);
        LocalDate dataFim = LocalDate.now().plusDays(12);

        List<Transferencia> transferencias = transferenciaRepository
                .findByDataTransferenciaBetween(dataInicio, dataFim);

        assertEquals(2, transferencias.size());

        assertTrue(transferencias.get(0).getDataTransferencia()
                .isBefore(transferencias.get(1).getDataTransferencia()) ||
                transferencias.get(0).getDataTransferencia()
                .isEqual(transferencias.get(1).getDataTransferencia()));
    }

    @Test
    @DisplayName("Deve contar transferências por conta de origem")
    void deveContarTransferenciasPorContaOrigem() {

        long count = transferenciaRepository.countByContaOrigem("1111111111");

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve retornar zero para conta inexistente")
    void deveRetornarZeroParaContaInexistente() {

        long count = transferenciaRepository.countByContaOrigem("9999999999");

        assertEquals(0, count);
    }

    @Test
    @DisplayName("Deve retornar lista vazia para período sem transferências")
    void deveRetornarListaVaziaParaPeriodoSemTransferencias() {

        LocalDate dataInicio = LocalDate.now().plusDays(100);
        LocalDate dataFim = LocalDate.now().plusDays(110);


        List<Transferencia> transferencias = transferenciaRepository
                .findByDataTransferenciaBetween(dataInicio, dataFim);


        assertTrue(transferencias.isEmpty());
    }

    @Test
    @DisplayName("Deve salvar transferência com todos os campos")
    void deveSalvarTransferenciaComTodosOsCampos() {

        Transferencia novaTransferencia = new Transferencia();
        novaTransferencia.setContaOrigem("6666666666");
        novaTransferencia.setContaDestino("7777777777");
        novaTransferencia.setValorTransferencia(new BigDecimal("750.00"));
        novaTransferencia.setTaxaTransferencia(new BigDecimal("9.00"));
        novaTransferencia.setDataTransferencia(LocalDate.now().plusDays(7));
        novaTransferencia.setDataAgendamento(LocalDate.now());


        Transferencia transferenciaSalva = transferenciaRepository.save(novaTransferencia);


        assertNotNull(transferenciaSalva.getId());
        assertEquals("6666666666", transferenciaSalva.getContaOrigem());
        assertEquals("7777777777", transferenciaSalva.getContaDestino());
        assertEquals(0, new BigDecimal("750.00").compareTo(transferenciaSalva.getValorTransferencia()));
        assertEquals(0, new BigDecimal("9.00").compareTo(transferenciaSalva.getTaxaTransferencia()));
        assertEquals(LocalDate.now().plusDays(7), transferenciaSalva.getDataTransferencia());
        assertEquals(LocalDate.now(), transferenciaSalva.getDataAgendamento());
    }

    @Test
    @DisplayName("Deve calcular dias para transferência automaticamente")
    void deveCalcularDiasParaTransferenciaAutomaticamente() {

        Transferencia novaTransferencia = new Transferencia();
        novaTransferencia.setContaOrigem("8888888888");
        novaTransferencia.setContaDestino("9999999999");
        novaTransferencia.setValorTransferencia(new BigDecimal("300.00"));
        novaTransferencia.setTaxaTransferencia(new BigDecimal("3.60"));
        novaTransferencia.setDataTransferencia(LocalDate.now().plusDays(3));
        novaTransferencia.setDataAgendamento(LocalDate.now());


        Transferencia transferenciaSalva = transferenciaRepository.save(novaTransferencia);


        assertEquals(3, transferenciaSalva.getDiasParaTransferencia());
    }
}

