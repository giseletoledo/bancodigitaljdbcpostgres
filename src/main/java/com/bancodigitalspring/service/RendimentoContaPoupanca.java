package com.bancodigitalspring.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.bancodigitalspring.model.Conta;
import com.bancodigitalspring.model.TipoTransacao;
import com.bancodigitalspring.model.Transacao;

public class RendimentoContaPoupanca implements RegraRendimento {

    @Override
    public void aplicarRendimento(Conta conta) {
        if (!"POUPANCA".equalsIgnoreCase(conta.getTipoConta())) return;

        // Define a taxa anual baseada no tipo do cliente
        BigDecimal taxaAnual = switch (conta.getCliente().getTipo()) {
            case COMUM -> new BigDecimal("0.005");
            case SUPER -> new BigDecimal("0.007");
            case PREMIUM -> new BigDecimal("0.009");
        };

        // Cálculo da taxa mensal: (1 + taxaAnual)^(1/12) - 1
        double taxaMensalDouble = Math.pow(1 + taxaAnual.doubleValue(), 1.0 / 12) - 1;
        BigDecimal taxaMensal = BigDecimal.valueOf(taxaMensalDouble);

        // Cálculo do rendimento
        BigDecimal rendimento = conta.getSaldo()
            .multiply(taxaMensal)
            .setScale(2, RoundingMode.HALF_EVEN);

        // Aplica o rendimento na conta
        conta.depositar(rendimento);
        conta.adicionarTransacao(new Transacao("Rendimento mensal", rendimento, TipoTransacao.DEPOSITO));
    }
}
