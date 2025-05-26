# Banco Digital API 🏦

API para gestão de clientes, contas bancárias e cartões com regras de negócio específicas.

## 📋 Funcionalidades
- **Cadastro de clientes** com validação de CPF, nome e idade
- **Gestão de contas** (Corrente e Poupança) com taxas diferenciadas por tipo de cliente
- **Operações bancárias**: depósito, saque, transferência e PIX
- **Cartões de crédito/débito** com limites customizados
- **Rendimentos automáticos** para conta poupança

## 🛠️ Tecnologias
- Java 17
- Spring Boot 3.2
- Spring JDBC
- PostgreSQL
- RESTful API

## 📊 Diagrama de Entidades
```mermaid
erDiagram
    CLIENTE ||--o{ CONTA : possui
    CONTA ||--o{ TRANSACAO : realiza
    CONTA ||--o{ CARTAO : possui
    CARTAO ||--o{ CARTAO_TRANSACAO : contém
