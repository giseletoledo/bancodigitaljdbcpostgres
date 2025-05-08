package com.bancodigitalspring.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Cartao {
    private Long id;
    private Long contaId;
    private String numeroCartao;
    private TipoCartao tipoCartao; // CREDITO ou DEBITO
    private BigDecimal limite;
    private BigDecimal limiteDiario; // só para débito
    private boolean ativo;
    private String senha;
    private List<CartaoTransacao> transacoes = new ArrayList<>();
    
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getContaId() {
		return contaId;
	}
	
	public void setContaId(Long contaId) {
		this.contaId = contaId;
	}
	
	public String getNumeroCartao() {
		return numeroCartao;
	}
	
	public void setNumeroCartao(String numeroCartao) {
		this.numeroCartao = numeroCartao;
	}
	
	public TipoCartao getTipoCartao() {
		return tipoCartao;
	}
	
	public void setTipoCartao(TipoCartao tipoCartao) {
		this.tipoCartao = tipoCartao;
	}
	
	public BigDecimal getLimite() {
		return limite;
	}
	
	public void setLimite(BigDecimal limite) {
		this.limite = limite;
	}
	
	public BigDecimal getLimiteDiario() {
		return limiteDiario;
	}
	
	public void setLimiteDiario(BigDecimal limiteDiario) {
		this.limiteDiario = limiteDiario;
	}
	
	public boolean isAtivo() {
		return ativo;
	}
	
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public List<CartaoTransacao> getTransacoes() {
		return transacoes;
	}
	
	public void setTransacoes(List<CartaoTransacao> transacoes) {
		this.transacoes = transacoes;
	}
    
}

