-- Script de dados iniciais para testes
-- Este arquivo é executado automaticamente pelo Spring Boot

-- Inserindo algumas transferências de exemplo para demonstração

CREATE TABLE transferencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    conta_origem VARCHAR(10) NOT NULL,
    conta_destino VARCHAR(10) NOT NULL,
    valor_transferencia DECIMAL(15,2) NOT NULL,
    taxa_transferencia DECIMAL(15,2) NOT NULL,
    data_transferencia DATE NOT NULL,
    data_agendamento DATE NOT NULL
);

INSERT INTO transferencias (conta_origem, conta_destino, valor_transferencia, taxa_transferencia, data_transferencia, data_agendamento)
VALUES
('1111111111', '2222222222', 1000.00, 12.00, '2025-07-26', '2025-07-21'),
('1111111111', '3333333333', 500.00, 6.00, '2025-07-31', '2025-07-20'),
('4444444444', '5555555555', 2000.00, 24.00, '2025-08-05', '2025-07-19');

-- Comentários sobre os dados de exemplo:
-- 1. Transferência para 1 dia: R$ 12,00 (valor mínimo) + 0% = R$ 12,00
-- 2. Transferência para 11 dias: R$ 0,00 + 8,2% de R$ 500,00 = R$ 41,00
-- 3. Transferência para 21 dias: R$ 0,00 + 6,9% de R$ 2000,00 = R$ 138,00
-- 4. Transferência para 41 dias: R$ 0,00 + 1,7% de R$ 750,00 = R$ 12,75

-- Nota: Os valores de taxa podem variar ligeiramente devido ao arredondamento