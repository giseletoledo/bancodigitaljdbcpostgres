package com.bancodigitalspring.service;

import com.bancodigitalspring.model.Conta;
import com.bancodigitalspring.model.TipoTransacao;
import com.bancodigitalspring.model.Transacao;

import java.math.BigDecimal;


public class RegraTaxaContaCorrente implements RegraTaxa {

    @Override
    public void aplicarTaxa(Conta conta) {
        if (!"CORRENTE".equalsIgnoreCase(conta.getTipoConta())) return;

        double taxa = switch (conta.getCliente().getTipo()) {
            case COMUM -> 12.0;
            case SUPER -> 8.0;
            case PREMIUM -> 0.0;
        };

        BigDecimal valorTaxa = BigDecimal.valueOf(taxa);

        if (conta.getSaldo().add(conta.getLimiteEspecial()).compareTo(valorTaxa) >= 0) {
            conta.setSaldo(conta.getSaldo().subtract(valorTaxa));
            conta.adicionarTransacao(new Transacao("Taxa de manutenção", valorTaxa, TipoTransacao.TAXA));
        }
    }
}

