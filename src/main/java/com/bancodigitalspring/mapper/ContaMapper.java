package com.bancodigitalspring.mapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.bancodigitalspring.dao.ClienteDAO;
import com.bancodigitalspring.dao.ContaDAO;
import com.bancodigitalspring.dto.ContaDTO;
import com.bancodigitalspring.model.Cliente;
import com.bancodigitalspring.model.Conta;

import com.bancodigitalspring.model.Transacao;
import com.bancodigitalspring.service.RegraRendimentoContaPoupanca;
import com.bancodigitalspring.service.RegraTaxaContaCorrente;

public class ContaMapper {

    public static ContaDTO toDTO(Conta conta) {
        return new ContaDTO(
            conta.getId(),
            conta.getNumero(),
            conta.getCliente().getId(),
            conta.getChavePix(),
            conta.getTipoConta(),
            conta.getLimiteEspecial()
        );
    }

    public static Conta fromResultSet(ResultSet rs, Connection connection) throws SQLException {
        Long id = rs.getLong("id");
        String numero = rs.getString("numero_conta");
        String chavePix = rs.getString("chave_pix");
        String tipo = rs.getString("tipo_conta");
        BigDecimal saldo = rs.getBigDecimal("saldo");
        BigDecimal limite = rs.getBigDecimal("limite_especial");

        Long clienteId = rs.getLong("cliente_id");
        ClienteDAO clienteDAO = new ClienteDAO();
        Cliente cliente = clienteDAO.buscarClientePorId(clienteId);

        Conta conta = new Conta(id, numero, cliente, chavePix, tipo, limite);
        conta.setSaldo(saldo);

        if ("CORRENTE".equalsIgnoreCase(tipo)) {
            conta.setRegraTaxa(new RegraTaxaContaCorrente());
        } else if ("POUPANCA".equalsIgnoreCase(tipo)) {
            conta.setRegraRendimento(new RegraRendimentoContaPoupanca());
        }

        // ✅ Buscar transações usando DAO
        ContaDAO contaDAO = new ContaDAO();
        List<Transacao> transacoes = contaDAO.buscarTransacoesPorContaId(id, connection);
        conta.getTransacoes().addAll(transacoes);

        return conta;
    }


}
