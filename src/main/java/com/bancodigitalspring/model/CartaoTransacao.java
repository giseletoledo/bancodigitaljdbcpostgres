package com.bancodigitalspring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartaoTransacao {
    private Long id;
    private Long cartaoId;
    private BigDecimal valor;
    private String descricao;
    private LocalDateTime dataTransacao;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getCartaoId() {
		return cartaoId;
	}
	public void setCartaoId(Long cartaoId) {
		this.cartaoId = cartaoId;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public LocalDateTime getDataTransacao() {
		return dataTransacao;
	}
	public void setDataTransacao(LocalDateTime dataTransacao) {
		this.dataTransacao = dataTransacao;
	}
}
