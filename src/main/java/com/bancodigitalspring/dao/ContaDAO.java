package com.bancodigitalspring.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.bancodigitalspring.config.DatabaseConfig;
import com.bancodigitalspring.mapper.ContaMapper;
import com.bancodigitalspring.model.Conta;
import com.bancodigitalspring.model.TipoTransacao;
import com.bancodigitalspring.model.Transacao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bancodigitalspring.exception.BancoDadosException;
import com.bancodigitalspring.exception.ChavePixNaoEncontradaException;
import com.bancodigitalspring.exception.ContaNaoEncontradaException;


@Repository
public class ContaDAO {

    private static final Logger logger = LoggerFactory.getLogger(ContaDAO.class);

    public void criarConta(Conta conta) {
        String sql = "SELECT criar_conta(?, ?, ?, ?, ?, ?)";

        logger.debug("Tentando criar conta para cliente ID: {}", conta.getCliente().getId());

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, conta.getCliente().getId());
            stmt.setBigDecimal(2, conta.getSaldo());
            stmt.setString(3, conta.getTipoConta());
            stmt.setString(4, conta.getChavePix());
            stmt.setString(5, conta.getNumero());
            stmt.setBigDecimal(6, conta.getLimiteEspecial());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    conta.setId(rs.getLong(1));
                    logger.info("Nova conta criada com ID: {}", conta.getId());
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao criar conta para cliente ID: {}", conta.getCliente().getId(), e);
            throw new BancoDadosException("Falha ao criar conta", e);
        }
    }

    public  Conta buscarContaPorId(Long id) {
        String sql = "SELECT id, numero_conta, cliente_id, saldo, tipo_conta, chave_pix, limite_especial FROM buscar_conta_por_id(?)";

        logger.debug("Buscando conta por ID: {}", id);

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Conta encontrada com ID: {}", id);
                    return ContaMapper.fromResultSet(rs, connection);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar conta por ID: {}", id, e);
            throw new BancoDadosException("Falha ao buscar conta", e);
        }

        logger.warn("Conta não encontrada com ID: {}", id);
        throw new ContaNaoEncontradaException(id);
    }

    public List<Conta> buscarContasPorClienteId(Long clienteId) {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT * FROM contas WHERE cliente_id = ?";
        logger.debug("Buscando contas para cliente ID: {}", clienteId);

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Conta conta = ContaMapper.fromResultSet(rs, connection);
                    contas.add(conta);
                }
            }
            logger.debug("Encontradas {} contas para cliente ID: {}", contas.size(), clienteId);
        } catch (SQLException e) {
            logger.error("Erro ao buscar contas para cliente ID: {}", clienteId, e);
            throw new BancoDadosException("Falha ao buscar contas do cliente", e);
        }

        return contas;
    }

    public Conta buscarContaPorChavePix(String chavePix) {
        String sql = "SELECT * FROM contas WHERE chave_pix = ?";
        logger.debug("Buscando conta por chave Pix: {}", chavePix);

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, chavePix);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Conta encontrada com chave Pix: {}", chavePix);
                    return ContaMapper.fromResultSet(rs, connection);
                }
            }
        } catch (SQLException e) {
            logger.error("Erro ao buscar conta por chave Pix: {}", chavePix, e);
            throw new BancoDadosException("Falha ao buscar conta por chave Pix", e);
        }

        logger.warn("Conta não encontrada com chave Pix: {}", chavePix);
        throw new ChavePixNaoEncontradaException(chavePix);
    }

    public void atualizarConta(Conta conta) {
        String sql = "UPDATE contas SET saldo = ?, limite_especial = ? WHERE id = ?";
        logger.debug("Atualizando conta ID: {}", conta.getId());

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setBigDecimal(1, conta.getSaldo());
            stmt.setBigDecimal(2, conta.getLimiteEspecial());
            stmt.setLong(3, conta.getId());

            int affectedRows = stmt.executeUpdate();
            logger.debug("Conta atualizada, linhas afetadas: {}", affectedRows);

            for (Transacao t : conta.getTransacoesNaoPersistidas()) {
                salvarTransacao(connection, t, conta.getId());
            }
            conta.limparTransacoesNaoPersistidas();
        } catch (SQLException e) {
            logger.error("Erro ao atualizar conta ID: {}", conta.getId(), e);
            throw new BancoDadosException("Falha ao atualizar conta", e);
        }
    }

    private void salvarTransacao(Connection connection, Transacao t, Long contaId) throws SQLException {
        String sql = "INSERT INTO transacoes (conta_id, valor, tipo_transacao, descricao) VALUES (?, ?, ?, ?)";
        logger.trace("Salvando transação para conta ID: {}", contaId);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, contaId);
            stmt.setBigDecimal(2, t.getValor());
            stmt.setString(3, t.getTipo().name());
            stmt.setString(4, t.getDescricao());
            stmt.executeUpdate();
        }
    }

    public List<Transacao> buscarTransacoesPorContaId(Long contaId, Connection connection) {
        List<Transacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM transacoes WHERE conta_id = ? ORDER BY data_transacao";
        logger.debug("Buscando transações para conta ID: {}", contaId);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, contaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Transacao transacao = new Transacao(
                            rs.getString("descricao"),
                            rs.getBigDecimal("valor"),
                            TipoTransacao.valueOf(rs.getString("tipo_transacao"))
                    );

                    Timestamp data = rs.getTimestamp("data_transacao");
                    if (data != null) {
                        transacao.setData(data.toLocalDateTime());
                    }

                    transacoes.add(transacao);
                }
            }
            logger.debug("Encontradas {} transações para conta ID: {}", transacoes.size(), contaId);
        } catch (SQLException e) {
            logger.error("Erro ao buscar transações para conta ID: {}", contaId, e);
            throw new BancoDadosException("Falha ao buscar transações", e);
        }
        return transacoes;
    }

    public boolean deletarConta(Long contaId) throws SQLException {
        String sql = "SELECT deletar_conta(?)";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, contaId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getBoolean(1);
            }
        }
    }
}
