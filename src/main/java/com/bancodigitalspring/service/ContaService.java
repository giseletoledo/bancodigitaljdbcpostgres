package com.bancodigitalspring.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bancodigitalspring.dao.ClienteDAO;
import com.bancodigitalspring.dao.ContaDAO;
import com.bancodigitalspring.dto.ContaDTO;
import com.bancodigitalspring.mapper.ContaMapper;
import com.bancodigitalspring.model.Cliente;
import com.bancodigitalspring.model.Conta;

@Service
public class ContaService {

    @Autowired
    private ContaDAO contaDAO;

    @Autowired
    private ClienteDAO clienteDAO;

    public void criarConta(ContaDTO dto) throws SQLException {
        Cliente cliente = clienteDAO.buscarClientePorId(dto.clienteId());
        if (cliente == null) throw new IllegalArgumentException("Cliente não encontrado.");

        Conta conta = new Conta(
                null,
                dto.numero(),
                cliente,
                dto.chavePix(),
                dto.tipo().toUpperCase(),
                dto.limiteEspecial() != null ? dto.limiteEspecial() : BigDecimal.ZERO
        );

        switch (dto.tipo().toUpperCase()) {
            case "CORRENTE" -> conta.setRegraTaxa(new RegraTaxaContaCorrente());
            case "POUPANCA" -> conta.setRegraRendimento(new RegraRendimentoContaPoupanca());
            default -> throw new IllegalArgumentException("Tipo de conta inválido.");
        }

        contaDAO.criarConta(conta);
    }

    public ContaDTO buscarContaPorId(Long id) throws SQLException {
        Conta conta = contaDAO.buscarContaPorId(id);
        return ContaMapper.toDTO(conta);
    }

    public BigDecimal consultarSaldo(Long id) throws SQLException {
        return contaDAO.buscarContaPorId(id).getSaldo();
    }

    public void depositar(Long id, BigDecimal valor) throws SQLException {
        Conta conta = contaDAO.buscarContaPorId(id);
        conta.depositar(valor);
        contaDAO.atualizarConta(conta);
    }

    public void sacar(Long id, BigDecimal valor) throws SQLException {
        Conta conta = contaDAO.buscarContaPorId(id);
        if (!conta.sacar(valor)) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }
        contaDAO.atualizarConta(conta);
    }

    public void transferir(Long origemId, Long destinoId, BigDecimal valor) throws SQLException {
        Conta origem = contaDAO.buscarContaPorId(origemId);
        Conta destino = contaDAO.buscarContaPorId(destinoId);

        origem.transferirPara(destino, valor);

        contaDAO.atualizarConta(origem);
        contaDAO.atualizarConta(destino);
    }

    public void realizarPix(Long origemId, String chavePixDestino, BigDecimal valor) throws SQLException {
        Conta origem = contaDAO.buscarContaPorId(origemId);
        Conta destino = contaDAO.buscarContaPorChavePix(chavePixDestino);

        if (destino == null) throw new IllegalArgumentException("Chave Pix não encontrada.");

        origem.transferirPara(destino, valor);

        contaDAO.atualizarConta(origem);
        contaDAO.atualizarConta(destino);
    }

    public String gerarExtratosCliente(Long clienteId) throws SQLException {
        List<Conta> contas = contaDAO.buscarContasPorClienteId(clienteId);
        StringBuilder sb = new StringBuilder();

        for (Conta conta : contas) {
            sb.append(conta.gerarExtrato()).append("\n\n");
        }

        return sb.toString();
    }

   
    public void aplicarTaxaManutencao(Long id) throws SQLException {
        Conta conta = contaDAO.buscarContaPorId(id);
        conta.aplicarTaxa();
        contaDAO.atualizarConta(conta);
    }

    public void aplicarRendimento(Long id) throws SQLException {
        Conta conta = contaDAO.buscarContaPorId(id);
        conta.aplicarRendimento();
        contaDAO.atualizarConta(conta);
    }

    public String gerarExtrato(Long id) throws SQLException {
        Conta conta = contaDAO.buscarContaPorId(id);
        return conta.gerarExtrato();
    }
}
