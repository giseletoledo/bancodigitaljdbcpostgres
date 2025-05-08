package com.bancodigitalspring.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ValidadorCliente {

	 public static boolean validarCPF(String cpf) {
	        cpf = cpf.replaceAll("[^0-9]", ""); // Remove pontos e traço
	        if (cpf.length() != 11) return false;

	        int soma1 = 0, soma2 = 0;
	        for (int i = 0; i < 9; i++) {
	            int digito = Character.getNumericValue(cpf.charAt(i));
	            soma1 += digito * (10 - i);
	            soma2 += digito * (11 - i);
	        }

	        int resto1 = (soma1 * 10) % 11;
	        if (resto1 == 10) resto1 = 0;

	        soma2 += resto1 * 2;
	        int resto2 = (soma2 * 10) % 11;
	        if (resto2 == 10) resto2 = 0;

	        return resto1 == Character.getNumericValue(cpf.charAt(9)) &&
	               resto2 == Character.getNumericValue(cpf.charAt(10));
	    }
	 
    // Validação do Nome (somente letras e espaços, 2 a 100 caracteres)
    public static boolean validarNome(String nome) {
        return nome != null && nome.matches("^[A-Za-zÀ-ÿ ]{2,100}$");
    }

    // Validação da Data de Nascimento (formato DD/MM/AAAA e maior de 18 anos)
    public static boolean validarDataNascimento(LocalDate dataNascimento) {
        try {
            LocalDate hoje = LocalDate.now();
            return dataNascimento != null && dataNascimento.isBefore(hoje.minusYears(18));
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    // Validação de CEP (formato xxxxx-xxx)
    public static boolean validarCEP(String cep) {
        return cep != null && cep.matches("\\d{5}-\\d{3}");
    }
}

