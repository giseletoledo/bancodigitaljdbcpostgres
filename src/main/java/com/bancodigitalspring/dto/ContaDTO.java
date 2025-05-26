package com.bancodigitalspring.dto;

import java.math.BigDecimal;

public record ContaDTO(
	    Long id,
	    String numero,
	    Long clienteId,
	    String chavePix,
	    String tipo,
	    BigDecimal limiteEspecial,
		BigDecimal saldo
) {}
