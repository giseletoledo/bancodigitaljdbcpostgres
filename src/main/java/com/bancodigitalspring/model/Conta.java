package com.bancodigitalspring.model;

import com.bancodigitalspring.service.RegraRendimento;
import com.bancodigitalspring.service.RegraTaxa;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

public class Conta {
	private Long id;
	private String numero;
    private Cliente cliente;
    private BigDecimal saldo = BigDecimal.ZERO;
    private BigDecimal limiteEspecial = BigDecimal.ZERO;
    private String chavePix;
    private String tipoConta; // "CORRENTE" ou "POUPANCA"
    private List<Transacao> transacoes = new ArrayList<>();
	
    
    private RegraTaxa regraTaxa;
    private RegraRendimento regraRendimento;

    public Conta(String numero, Cliente cliente, String chavePix, String tipoConta, BigDecimal limiteEspecial) {
        this.numero = numero;
        this.cliente = cliente;
        this.chavePix = chavePix;
        this.tipoConta = tipoConta;
        this.limiteEspecial = limiteEspecial != null ? limiteEspecial : BigDecimal.ZERO;
    }
    
    public Conta(Long id, String numero, Cliente cliente, String chavePix, String tipoConta, BigDecimal limiteEspecial) {
        this.id = id;
        this.numero = numero;
        this.cliente = cliente;
        this.chavePix = chavePix;
        this.tipoConta = tipoConta;
        this.limiteEspecial = limiteEspecial != null ? limiteEspecial : BigDecimal.ZERO;
    }
    

    // --- Getters e Setters ---
	public Long getId() { return id; }
	
    public void setId(Long id) {
		this.id = id;
	}

	public String getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public BigDecimal getLimiteEspecial() {
		return limiteEspecial;
	}

	public void setLimiteEspecial(BigDecimal limiteEspecial) {
		this.limiteEspecial = limiteEspecial;
	}

	public String getChavePix() { return chavePix; }

    public String getTipoConta() { return tipoConta; }
    public List<Transacao> getTransacoes() { return transacoes; }

    public void setChavePix(String chavePix) {
        this.chavePix = chavePix;
    }

    public void depositar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido para depósito.");
        }

        saldo = saldo.add(valor);
        adicionarTransacao(new Transacao("Depósito", valor, TipoTransacao.DEPOSITO));
    }

    public boolean sacar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido para saque.");
        }

        BigDecimal saldoDisponivel = saldo.add(limiteEspecial);
        if (saldoDisponivel.compareTo(valor) >= 0) {
            saldo = saldo.subtract(valor);
            adicionarTransacao(new Transacao("Saque", valor, TipoTransacao.SAQUE));
            return true;
        }
        return false;
    }

    public void transferirPara(Conta destino, BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor inválido para transferência.");
        }

        if (this.sacar(valor)) {
            destino.depositar(valor);
            adicionarTransacao(new Transacao(
                "Transferência para conta " + destino.getNumero(),
                valor,
                TipoTransacao.TRANSFERENCIA
            ));
        } else {
            throw new RuntimeException("Saldo insuficiente para transferência.");
        }
    }


    // --- Regras bancárias ---
    // Setters de regra
    public void setRegraTaxa(RegraTaxa regraTaxa) {
        this.regraTaxa = regraTaxa;
    }

    public void setRegraRendimento(RegraRendimento regraRendimento) {
        this.regraRendimento = regraRendimento;
    }

    // Aplicações de regras
    public void aplicarTaxa() {
        if (regraTaxa != null) regraTaxa.aplicarTaxa(this);
    }

    public void aplicarRendimento() {
        if (regraRendimento != null) regraRendimento.aplicarRendimento(this);
    }
    
    private List<Transacao> transacoesNaoPersistidas = new ArrayList<>();
    
    public void adicionarTransacao(Transacao transacao) {
        transacoes.add(transacao);                    // mostra no extrato
        transacoesNaoPersistidas.add(transacao);      // salva no banco
    }
  
    public List<Transacao> getTransacoesNaoPersistidas() {
        return new ArrayList<>(transacoesNaoPersistidas);
    }

    public void limparTransacoesNaoPersistidas() {
        transacoesNaoPersistidas.clear();
    }


    // --- Extrato resumido ---
    public String gerarExtrato() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Extrato da Conta ===\n");
        sb.append("Número: ").append(numero).append("\n");
        sb.append("Cliente: ").append(cliente.getNome()).append("\n");
        sb.append("Tipo de Conta: ").append(tipoConta).append("\n");
        sb.append("Saldo Atual: R$ ").append(String.format("%.2f", saldo)).append("\n\n");

        sb.append("--- Transações ---\n");
        if (transacoes.isEmpty()) {
            sb.append("Nenhuma transação registrada.\n");
        } else {
            for (Transacao t : transacoes) {
                sb.append(t.getData()).append(" - ").append(t.getTipo()).append(" - R$ ")
                  .append(String.format("%.2f", t.getValor())).append(" (")
                  .append(t.getDescricao()).append(")\n");
            }
        }
        return sb.toString();
    }
}
