package com.bancodigitalspring.exception;

public class BancoDadosException extends RuntimeException {

    public BancoDadosException(String mensagem) {
        super(mensagem);
    }
    public BancoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}