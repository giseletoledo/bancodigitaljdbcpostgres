package com.bancodigitalspring.exception;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(Long id) {
        super("Conta com ID " + id + " não encontrada");
    }
}