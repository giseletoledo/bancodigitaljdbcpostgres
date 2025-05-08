package com.bancodigitalspring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public record ClienteDTO(
        Long id,
        String nome,
        String cpf,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate dataNascimento,
        EnderecoDTO endereco,
        String tipoCliente
) {

    // Construtor adicional que aceita String para dataNascimento
    public ClienteDTO(Long id, String nome, String cpf, String dataNascimento,
                      String tipoCliente, String rua, String numero, String complemento,
                      String cidade, String estado, String cep) {
        this(
                id,
                nome,
                cpf,
                LocalDate.parse(dataNascimento, DateTimeFormatter.ISO_LOCAL_DATE), // Converte String para LocalDate
                new EnderecoDTO(rua, numero, complemento, cidade, estado, cep),
                tipoCliente
        );
    }

    // Metodo para formatar a data
    public String getDataNascimentoFormatada() {
        return dataNascimento.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}

