# Sistema de Agendamento de Transferências Financeiras

Um sistema robusto e escalável para agendamento de transferências financeiras, desenvolvido com Spring Boot e Angular, que utiliza o padrão Strategy para cálculo dinâmico de taxas e adere aos princípios SOLID e às práticas de Clean Code.

## Visão Geral

O Sistema de Agendamento de Transferências Financeiras é uma aplicação web full-stack que permite aos usuários agendar transferências entre contas bancárias de forma prática, com cálculo automático de taxas com base na data da operação. A plataforma oferece uma interface amigável para gerenciar agendamentos e consultar o histórico de transferências.

### Funcionalidades Principais

- **Agendamento de Transferências**: Interface para criar novos agendamentos com validação em tempo real
- **Cálculo Automático de Taxas**: Sistema baseado no padrão Strategy que calcula taxas conforme regras de negócio
- **Extrato de Transferências**: Visualização completa do histórico com filtros e ordenação
- **Validações Robustas**: Validação de formatos de conta, valores e datas
- **Interface Responsiva**: Design adaptável para desktop e mobile
- **Exportação de Dados**: Funcionalidade para exportar extratos em formato CSV


## Arquitetura

O sistema segue uma arquitetura em camadas com separação clara de responsabilidades, implementando os princípios SOLID.

### Arquitetura Geral

```
┌─────────────────────────────────────────────────────────────┐
│                    Frontend (Angular)                       │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Components    │  │    Services     │  │    Models    │ │
│  │                 │  │                 │  │              │ │
│  │ • Agendamento   │  │ • Transferencia │  │ • Interfaces │ │
│  │ • Extrato       │  │   Service       │  │ • DTOs       │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ HTTP/REST API
                                │
┌─────────────────────────────────────────────────────────────┐
│                   Backend (Spring Boot)                     │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   Controllers   │  │    Services     │  │ Repositories │ │
│  │                 │  │                 │  │              │ │
│  │ • REST APIs     │  │ • Business      │  │ • Data       │ │
│  │ • Validation    │  │   Logic         │  │   Access     │ │
│  │ • Exception     │  │ • Strategy      │  │ • JPA        │ │
│  │   Handling      │  │   Pattern       │  │              │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                    Database (H2)                            │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │                 Transferencias                          │ │
│  │ • id, conta_origem, conta_destino                       │ │
│  │ • valor_transferencia, taxa_transferencia               │ │
│  │ • data_agendamento, data_transferencia                  │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### Padrão Strategy - Cálculo de Taxas

O sistema implementa o padrão Strategy para o cálculo de taxas de transferência, permitindo flexibilidade e extensibilidade nas regras de negócio:

```
┌─────────────────────────────────────────────────────────────┐
│                 TaxaCalculationContext                      │
├─────────────────────────────────────────────────────────────┤
│ + calcularTaxa(valor, dias): BigDecimal                     │
│ + isTransferenciaPermitida(dias): boolean                   │
└─────────────────────────────────────────────────────────────┘
                                │
                                │ uses
                                ▼
┌─────────────────────────────────────────────────────────────┐
│              TaxaCalculationStrategy                        │
├─────────────────────────────────────────────────────────────┤
│ + calcularTaxa(valor, dias): BigDecimal                     │
│ + isAplicavel(dias): boolean                                │
│ + getDescricao(): String                                    │
└─────────────────────────────────────────────────────────────┘
                                ▲
                                │ implements
                ┌───────────────┼───────────────┐
                │               │               │
┌───────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│TaxaDiaZeroStrategy│ │TaxaDias1a10Strategy│ │TaxaDias11a20Strategy│
├───────────────────┤ ├─────────────────┤ ├─────────────────┤
│ 	2,5%       		0%     		 8,2%  
└───────────────────┘ └─────────────────┘ └─────────────────┘
```


## Tecnologias Utilizadas

### Backend
- **Java 17**: Linguagem de programação principal
- **Spring Boot 3.2.0**: Framework para desenvolvimento de aplicações Java
- **Spring Data JPA**: Abstração para acesso a dados
- **Spring Web**: Desenvolvimento de APIs REST
- **Spring Validation**: Validação de dados
- **H2 Database**: Banco de dados em memória para desenvolvimento
- **Maven**: Gerenciamento de dependências e build
- **JUnit 5**: Framework de testes unitários
- **Mockito**: Framework para mocking em testes


### Frontend
- **Angular 18**: Framework para desenvolvimento web
- **TypeScript**: Linguagem de programação tipada
- **Tailwind CSS 3.4.0**: Framework CSS utilitário
- **RxJS**: Programação reativa
- **Angular Reactive Forms**: Formulários reativos
- **Node.js 20.18.0**: Runtime JavaScript
- **npm**: Gerenciador de pacotes

### Ferramentas de Desenvolvimento
- **Git**: Controle de versão
- **IntelliJ IDEA**: IDE recomendada para desenvolvimento
- **VS Code**: Editor alternativo para frontend
- **Postman**: Testes de API (opcional)

### Padrões e Princípios
- **SOLID**: Princípios de design orientado a objetos
- **Clean Code**: Código limpo e legível
- **Strategy Pattern**: Padrão para cálculo de taxas
- **MVC**: Model-View-Controller
- **REST**: Architectural style para APIs
- **Responsive Design**: Design adaptável
- **Component-Based Architecture**: Arquitetura baseada em componentes


## Pré-requisitos

Antes de executar o projeto, certifique-se de ter as seguintes ferramentas instaladas:

### Requisitos Obrigatórios
- **Java 17 ou superior**: [Download OpenJDK](https://openjdk.org/projects/jdk/17/)
- **Node.js 18+ e npm**: [Download Node.js](https://nodejs.org/)
- **Git**: [Download Git](https://git-scm.com/)

### Verificação da Instalação
```bash
# Verificar Java
java -version

# Verificar Node.js e npm
node -v
npm -v

# Verificar Git
git --version
```

### Ferramentas Opcionais
- **IntelliJ IDEA**: Para desenvolvimento backend
- **VS Code**: Para desenvolvimento frontend
- **Maven**

## Instalação e Configuração
```bash
### 1. Clone do Repositório

git clone https://github.com/gc-softdev/sistema-transferencias.git
cd sistema-transferencias
```

### 2. Configuração do Backend

#### Instalação de Dependências
```bash
cd backend
./mvnw clean install
```

# Configurações da Aplicação
server.port=8080
spring.application.name=sistema-transferencias
```

### Execução em Desenvolvimento

#### 1. Iniciar o Backend

cd backend
./mvnw spring-boot:run
```

O backend estará disponível em: `http://localhost:8080`

**Endpoints principais:**
- API Base: `http://localhost:8080/api/transferencias`
- H2 Console: `http://localhost:8080/h2-console`
- Health Check: `http://localhost:8080/api/transferencias/health`

#### 2. Iniciar o Frontend
```bash
cd frontend/sistema-transferencias
ng serve
```

O frontend estará disponível em: `http://localhost:4200`



### Verificação da Execução

1. **Backend**: Acesse `http://localhost:8080/api/transferencias/health`
2. **Frontend**: Acesse `http://localhost:4200`
3. **Integração**: Teste o agendamento de uma transferência


## Documentação API

### Base URL
```
http://localhost:8080/api/transferencias
```

### Endpoints

#### 1. Agendar Transferência
```http
POST /api/transferencias
Content-Type: application/json

{
  "contaOrigem": "1234567890",
  "contaDestino": "0987654321",
  "valorTransferencia": 1000.00,
  "dataTransferencia": "2025-07-25"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "contaOrigem": "1234567890",
  "contaDestino": "0987654321",
  "valorTransferencia": 1000.00,
  "taxaTransferencia": 12.00,
  "dataTransferencia": "2025-07-25",
  "dataAgendamento": "2025-07-21",
  "diasParaTransferencia": 4
}
```

#### 2. Buscar Todas as Transferências
```http
GET /api/transferencias
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "contaOrigem": "1234567890",
    "contaDestino": "0987654321",
    "valorTransferencia": 1000.00,
    "taxaTransferencia": 12.00,
    "dataTransferencia": "2025-07-25",
    "dataAgendamento": "2025-07-21",
    "diasParaTransferencia": 4
  }
]
```

#### 3. Buscar Transferência por ID
```http
GET /api/transferencias/{id}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "contaOrigem": "1234567890",
  "contaDestino": "0987654321",
  "valorTransferencia": 1000.00,
  "taxaTransferencia": 12.00,
  "dataTransferencia": "2025-07-25",
  "dataAgendamento": "2025-07-21",
  "diasParaTransferencia": 4
}
```

#### 4. Buscar Transferências por Conta
```http
GET /api/transferencias/conta/{contaOrigem}
```

#### 5. Calcular Taxa
```http
GET /api/transferencias/calcular-taxa?valor=1000&dataTransferencia=2025-07-25
```

**Response (200 OK):**
```json
{
  "valorTransferencia": 1000.00,
  "dataTransferencia": "2025-07-25",
  "taxaCalculada": 12.00,
  "diasParaTransferencia": 4
}
```

#### 6. Health Check
```http
GET /api/transferencias/health
```

**Response (200 OK):**
```json
{
  "status": "UP",
  "service": "Sistema de Transferências",
  "timestamp": "2025-07-21T13:00:00Z"
}
```

### Validações

#### Conta Origem/Destino
- Formato: Exatamente 10 dígitos numéricos
- Exemplo válido: `1234567890`
- Exemplo inválido: `123456789` (9 dígitos)

#### Valor da Transferência
- Mínimo: R$ 0,01
- Máximo: Sem limite definido
- Formato: Decimal com até 2 casas

#### Data da Transferência
- Deve ser uma data futura
- Formato: `YYYY-MM-DD`
- Máximo: 50 dias a partir da data atual

### Exemplos de Erro

#### Erro de Validação (400)
```json
{
  "timestamp": "2025-07-21T13:00:00Z",
  "status": 400,
  "error": "Erro de validação",
  "message": "Dados inválidos",
  "errors": {
    "contaOrigem": "Conta de origem deve ter exatamente 10 dígitos",
    "valorTransferencia": "Valor deve ser maior que zero"
  }
}
```

#### Erro de Taxa (400)
```json
{
  "timestamp": "2025-07-21T13:00:00Z",
  "status": 400,
  "error": "Erro no cálculo da taxa",
  "message": "Transferência não permitida para 60 dias. Máximo permitido: 50 dias."
}
```


## Decisões Arquiteturais

### Padrão Strategy para Cálculo de Taxas

**Decisão**: Implementar o padrão Strategy para o cálculo de taxas de transferência.

**Justificativa**: O padrão Strategy foi escolhido devido aos seguintes benefícios:

1. **Flexibilidade**: Permite adicionar novas regras de cálculo sem modificar código existente
2. **Manutenibilidade**: Cada estratégia é uma classe independente, facilitando manutenção
3. **Testabilidade**: Cada estratégia pode ser testada isoladamente
4. **Extensibilidade**: Novas faixas de dias podem ser adicionadas facilmente
5. **Princípio Aberto/Fechado**: Aberto para extensão, fechado para modificação

**Implementação**:
```java
public interface TaxaCalculationStrategy {
    BigDecimal calcularTaxa(BigDecimal valorTransferencia, int diasParaTransferencia);
    boolean isAplicavel(int diasParaTransferencia);
    String getDescricao();
}
```

### Arquitetura MVC com Spring Boot

**Decisão**: Utilizar arquitetura MVC (Model-View-Controller) com Spring Boot.

**Justificativa**:
- **Separação de Responsabilidades**: Clara divisão entre camadas
- **Maturidade**: Framework maduro e amplamente adotado
- **Produtividade**: Auto-configuração e convenções reduzem boilerplate
- **Ecossistema**: Vasta gama de integrações disponíveis
- **Comunidade**: Grande comunidade e documentação extensa

### Banco de Dados H2 em Memória

**Decisão**: Utilizar H2 Database em memória para desenvolvimento.

**Justificativa**:
- **Simplicidade**: Não requer instalação ou configuração externa
- **Velocidade**: Execução rápida para desenvolvimento e testes
- **Portabilidade**: Facilita execução em diferentes ambientes
- **Desenvolvimento**: Console web integrado para debugging

**Considerações para Produção**:
```properties
# Para produção, migrar para PostgreSQL ou MySQL
spring.datasource.url=jdbc:postgresql://localhost:5432/transferencias
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
```

### Frontend Angular com Componentes Standalone

**Decisão**: Utilizar Angular com componentes standalone e arquitetura reativa.

**Justificativa**:
- **Modernidade**: Componentes standalone são a abordagem mais recente
- **Simplicidade**: Reduz complexidade de módulos
- **Performance**: Lazy loading mais eficiente
- **Manutenibilidade**: Dependências explícitas por componente

### Tailwind CSS para Estilização

**Decisão**: Utilizar Tailwind CSS em vez de frameworks como Bootstrap.

**Justificativa**:
- **Utility-First**: Abordagem mais flexível e customizável
- **Performance**: CSS otimizado com purge de classes não utilizadas
- **Consistência**: Design system integrado
- **Produtividade**: Desenvolvimento mais rápido com classes utilitárias

### Validações Duplas (Frontend + Backend)

**Decisão**: Implementar validações tanto no frontend quanto no backend.

**Justificativa**:
- **Segurança**: Backend sempre valida dados recebidos
- **UX**: Frontend fornece feedback imediato ao usuário
- **Robustez**: Proteção contra manipulação de dados no cliente
- **Confiabilidade**: Garantia de integridade dos dados

### Tratamento de Exceções Centralizado

**Decisão**: Implementar `@ControllerAdvice` para tratamento global de exceções.

**Justificativa**:
- **Consistência**: Respostas de erro padronizadas
- **Manutenibilidade**: Lógica de tratamento centralizada
- **Logging**: Controle centralizado de logs de erro
- **API Design**: Respostas estruturadas para o frontend

### Testes Abrangentes

**Decisão**: Implementar testes unitários e de integração com alta cobertura.

**Justificativa**:
- **Qualidade**: Garantia de funcionamento correto
- **Refatoração**: Segurança para mudanças futuras

### Estrutura de DTOs

**Decisão**: Utilizar DTOs (Data Transfer Objects) para comunicação entre camadas.

**Justificativa**:
- **Encapsulamento**: Controle sobre dados expostos
- **Versionamento**: Facilita evolução da API
- **Validação**: Validações específicas por contexto
- **Serialização**: Controle sobre formato JSON




