package com.bancodigitalspring.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.bancodigitalspring.config.DatabaseConfig;
import com.bancodigitalspring.model.Cartao;
import com.bancodigitalspring.model.CartaoTransacao;
import com.bancodigitalspring.model.TipoCartao;
import org.springframework.stereotype.Repository;




@Repository
public class CartaoDAO {

    public void criarCartao(Cartao cartao) throws SQLException {
        String sql = """
            INSERT INTO cartoes (conta_id, numero_cartao, tipo_cartao, limite, limite_diario, senha, ativo)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, cartao.getContaId());
            stmt.setString(2, cartao.getNumeroCartao());
            stmt.setString(3, cartao.getTipoCartao().name());
            stmt.setBigDecimal(4, cartao.getLimite());
            stmt.setBigDecimal(5, cartao.getLimiteDiario());
            stmt.setString(6, cartao.getSenha());
            stmt.setBoolean(7, cartao.isAtivo());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                cartao.setId(rs.getLong(1));
            }
        }
    }

    public Cartao buscarCartaoPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM cartoes WHERE id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Cartao cartao = new Cartao();
                cartao.setId(rs.getLong("id"));
                cartao.setContaId(rs.getLong("conta_id"));
                cartao.setNumeroCartao(rs.getString("numero_cartao"));
                cartao.setTipoCartao(TipoCartao.valueOf(rs.getString("tipo_cartao")));
                cartao.setLimite(rs.getBigDecimal("limite"));
                cartao.setLimiteDiario(rs.getBigDecimal("limite_diario"));
                cartao.setSenha(rs.getString("senha"));
                cartao.setAtivo(rs.getBoolean("ativo"));
                return cartao;
            }
        }

        throw new IllegalArgumentException("Cartão não encontrado.");
    }

    public void registrarTransacao(Long cartaoId, BigDecimal valor, String descricao) throws SQLException {
        String sql = "INSERT INTO cartao_transacoes (cartao_id, valor, descricao) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, cartaoId);
            stmt.setBigDecimal(2, valor);
            stmt.setString(3, descricao);
            stmt.executeUpdate();
        }
    }

    public BigDecimal calcularTotalGastoNoMes(Long cartaoId) throws SQLException {
        String sql = """
            SELECT SUM(valor) as total
            FROM cartao_transacoes
            WHERE cartao_id = ? AND MONTH(data_transacao) = MONTH(CURRENT_DATE()) AND YEAR(data_transacao) = YEAR(CURRENT_DATE())
        """;

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, cartaoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }

    public void atualizarLimite(Long id, BigDecimal novoLimite) throws SQLException {
        String sql = "UPDATE cartoes SET limite = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, novoLimite);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarStatus(Long id, boolean ativo) throws SQLException {
        String sql = "UPDATE cartoes SET ativo = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBoolean(1, ativo);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarSenha(Long id, String novaSenha) throws SQLException {
        String sql = "UPDATE cartoes SET senha = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novaSenha);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public void atualizarLimiteDiario(Long id, BigDecimal novoLimite) throws SQLException {
        String sql = "UPDATE cartoes SET limite_diario = ? WHERE id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, novoLimite);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public List<CartaoTransacao> buscarTransacoesPorCartao(Long cartaoId) throws SQLException {
        List<CartaoTransacao> transacoes = new ArrayList<>();
        String sql = "SELECT * FROM cartao_transacoes WHERE cartao_id = ? ORDER BY data_transacao DESC";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, cartaoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CartaoTransacao transacao = new CartaoTransacao();
                transacao.setId(rs.getLong("id"));
                transacao.setCartaoId(rs.getLong("cartao_id"));
                transacao.setValor(rs.getBigDecimal("valor"));
                transacao.setDescricao(rs.getString("descricao"));
                transacao.setDataTransacao(rs.getTimestamp("data_transacao").toLocalDateTime());

                transacoes.add(transacao);
            }
        }

        return transacoes;
    }

    public void registrarPagamento(Long cartaoId, BigDecimal valor, String descricao) throws SQLException {
        String buscarSql = "SELECT * FROM cartoes WHERE id = ?";
        String transacaoSql = "INSERT INTO cartao_transacoes (cartao_id, valor, descricao, data_transacao) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        String atualizarLimiteSql = "UPDATE cartoes SET limite = limite - ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.conectar();
             PreparedStatement buscarStmt = conn.prepareStatement(buscarSql);
             PreparedStatement transacaoStmt = conn.prepareStatement(transacaoSql);
             PreparedStatement atualizarStmt = conn.prepareStatement(atualizarLimiteSql)) {

            // Buscar cartão
            buscarStmt.setLong(1, cartaoId);
            ResultSet rs = buscarStmt.executeQuery();
            if (!rs.next()) {
                throw new IllegalArgumentException("Cartão não encontrado.");
            }

            boolean ativo = rs.getBoolean("ativo");
            BigDecimal limite = rs.getBigDecimal("limite");

            if (!ativo) {
                throw new IllegalArgumentException("Cartão inativo.");
            }

            if (limite.compareTo(valor) < 0) {
                throw new IllegalArgumentException("Limite insuficiente.");
            }

            // Inserir transação
            transacaoStmt.setLong(1, cartaoId);
            transacaoStmt.setBigDecimal(2, valor);
            transacaoStmt.setString(3, descricao);
            transacaoStmt.executeUpdate();

            // Atualizar limite
            atualizarStmt.setBigDecimal(1, valor);
            atualizarStmt.setLong(2, cartaoId);
            atualizarStmt.executeUpdate();
        }
    }

}
