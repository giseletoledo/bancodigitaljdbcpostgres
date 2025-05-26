package com.bancodigitalspring.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transacao {
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private TipoTransacao tipo; // "DEPOSITO", "SAQUE", "TAXA", "RENDIMENTO","GASTO","PAGAMENTO","TRANSFERENCIA"
    private LocalDateTime data;

    public Transacao(String descricao, BigDecimal valor, TipoTransacao tipo) {
            this.descricao = descricao;
            this.valor = valor;
            this.tipo = tipo;
            this.data = LocalDateTime.now();
        }

        public BigDecimal getValor() {
            return valor;
        }

        public TipoTransacao getTipo() {
            return tipo;
        }

        public String getDescricao() {
            return descricao;
        }

        public LocalDateTime getData() {
            return data;
        }
        
        public void setData(LocalDateTime data) {
            this.data = data;
        }


		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}

}
