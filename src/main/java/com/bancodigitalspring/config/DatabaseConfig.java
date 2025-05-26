package com.bancodigitalspring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/bancodigital";
    private static final String USER = "postgres_user"; // usuário padrão do PostgreSQL
    private static final String PASSWORD = "banco123"; // altere para sua senha
    private static final Logger log = LoggerFactory.getLogger(DatabaseConfig.class);

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void criarTabelas() {
        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            //Criar tabelas do cliente
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS clientes (
                            id BIGSERIAL PRIMARY KEY,
                            nome VARCHAR(100) NOT NULL,
                            cpf VARCHAR(11) UNIQUE NOT NULL,
                            data_nascimento DATE NOT NULL,
                            tipo_cliente VARCHAR(20) NOT NULL
                        );
                    """);

            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS enderecos (
                            id BIGSERIAL PRIMARY KEY,
                            cliente_id BIGINT NOT NULL,
                            rua VARCHAR(100) NOT NULL,
                            numero VARCHAR(10) NOT NULL,
                            complemento VARCHAR(50),
                            cidade VARCHAR(100) NOT NULL,
                            estado VARCHAR(2) NOT NULL,
                            cep VARCHAR(9) NOT NULL,
                            FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
                        );
                    """);

            // Criar a tabela Contas
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS contas (
                            id BIGSERIAL PRIMARY KEY,
                            numero_conta VARCHAR(20) NOT NULL UNIQUE,
                            cliente_id BIGINT NOT NULL,
                            saldo DECIMAL(15,2) DEFAULT 0.00,
                            tipo_conta VARCHAR(20) NOT NULL,
                            chave_pix VARCHAR(100),
                            limite_especial DECIMAL(15,2) DEFAULT 0.00,
                            FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
                        );
                    """);

            // Criar a tabela Cartões
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS cartoes (
                            id BIGSERIAL PRIMARY KEY,
                            conta_id BIGINT NOT NULL,
                            tipo_cartao VARCHAR(20) NOT NULL,
                            numero_cartao VARCHAR(16) UNIQUE NOT NULL,
                            limite DECIMAL(15,2) DEFAULT 0.00,
                            status VARCHAR(10) DEFAULT 'ATIVO',
                            senha VARCHAR(10),
                            limite_diario DECIMAL(15,2),
                            FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE
                        );
                    """);

            // Criar a tabela Transações
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS transacoes (
                            id BIGSERIAL PRIMARY KEY,
                            conta_id BIGINT NOT NULL,
                            valor DECIMAL(15,2) NOT NULL,
                            tipo_transacao VARCHAR(20) NOT NULL,
                            descricao VARCHAR(255),
                            data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (conta_id) REFERENCES contas(id) ON DELETE CASCADE
                        );
                    """);

            // Criar a tabela Transações de Cartão
            stmt.execute("""
                        CREATE TABLE IF NOT EXISTS cartao_transacoes (
                            id BIGSERIAL PRIMARY KEY,
                            cartao_id BIGINT NOT NULL,
                            valor DECIMAL(15,2) NOT NULL,
                            descricao VARCHAR(255) NOT NULL,
                            data_transacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (cartao_id) REFERENCES cartoes(id) ON DELETE CASCADE
                        );
                    """);

            //System.out.println("Tabelas criadas com sucesso!");
            log.info("Tabelas criadas com sucesso!");
        } catch (SQLException e) {
            //e.printStackTrace();
            throw new RuntimeException("Erro ao criar tabelas no banco de dados.");
        }
    }
}

