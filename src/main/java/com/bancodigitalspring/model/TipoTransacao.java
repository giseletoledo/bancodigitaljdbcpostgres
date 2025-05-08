package com.bancodigitalspring.model;

public enum TipoTransacao {
    GASTO,         // Gasto realizado com o cartão de crédito
    TAXA,          // Taxa aplicada (ex.: taxa de utilização)
    PAGAMENTO,     // Pagamento da fatura do cartão
    DEPOSITO,      // Depósito em conta
    SAQUE,         // Saque da conta
    TRANSFERENCIA  // Transferência entre contas
}

