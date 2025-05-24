package com.bancodigitalspring.mapper;

import com.bancodigitalspring.model.Cliente;
import com.bancodigitalspring.model.Endereco;
import com.bancodigitalspring.model.TipoCliente;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClienteMapperDB {

    public static Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("cliente_id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setTipo(TipoCliente.valueOf(rs.getString("tipo_cliente")));

        Long enderecoId = rs.getLong("endereco_id");
        if (!rs.wasNull()) {
            Endereco endereco = new Endereco(
                    rs.getString("rua"),
                    rs.getString("numero"),
                    rs.getString("complemento"),
                    rs.getString("cidade"),
                    rs.getString("estado"),
                    rs.getString("cep")
            );
            endereco.setId(enderecoId);
            endereco.setClienteId(cliente.getId());

            cliente.setEndereco(endereco);
        }

        return cliente;
    }
}

