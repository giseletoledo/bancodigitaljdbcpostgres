package com.bancodigitalspring.dto;

import com.bancodigitalspring.model.TipoCartao;

import java.math.BigDecimal;

public record CartaoDTO(
		Long id,
		Long contaId,
		String numeroCartao,
		TipoCartao tipoCartao, // "CREDITO" ou "DEBITO"
		BigDecimal limite,
		BigDecimal limiteDiario,
		boolean ativo,
		String senha
	) {}


