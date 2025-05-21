package com.bancodigitalspring.dao;

import com.bancodigitalspring.config.DatabaseConfig;
import com.bancodigitalspring.model.Cliente;
import com.bancodigitalspring.model.Endereco;
import com.bancodigitalspring.model.TipoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class ClienteDAO {

    public void criarCliente(Cliente cliente) throws SQLException {
        String sql = "SELECT criar_cliente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (cliente.getEndereco() == null) {
                throw new SQLException("Endereço é obrigatório para criar um cliente");
            }

            Endereco endereco = cliente.getEndereco();

            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(4, cliente.getTipo().name());
            stmt.setString(5, endereco.getRua());
            stmt.setString(6, endereco.getNumero());
            stmt.setString(7, endereco.getComplemento());
            stmt.setString(8, endereco.getCidade());
            stmt.setString(9, endereco.getEstado());
            stmt.setString(10, endereco.getCep());

            // Apenas executa - não precisa do ResultSet
            stmt.execute();
        }
    }

    public List<Cliente> listarClientes() throws SQLException {
        String sql = "SELECT * FROM listar_todos_clientes()";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }
        return clientes;
    }

    public void atualizarCliente(Cliente cliente) throws SQLException {
        String sql = "SELECT atualizar_cliente(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Verificar endereço obrigatório
            if (cliente.getEndereco() == null) {
                throw new SQLException("Endereço é obrigatório");
            }

            Endereco endereco = cliente.getEndereco();

            // Setar parâmetros
            stmt.setLong(1, cliente.getId());
            stmt.setString(2, cliente.getNome());
            stmt.setDate(3, Date.valueOf(cliente.getDataNascimento()));
            stmt.setString(4, cliente.getTipo().name());
            stmt.setString(5, endereco.getRua());
            stmt.setString(6, endereco.getNumero());
            stmt.setString(7, endereco.getComplemento());
            stmt.setString(8, endereco.getCidade());
            stmt.setString(9, endereco.getEstado());
            stmt.setString(10, endereco.getCep());

            stmt.executeQuery();
        }
    }

    public void deletarCliente(Long id) throws SQLException {
        String sql = "SELECT deletar_cliente(?)";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeQuery();
        }
    }
    
    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getLong("cliente_id")); // Assumindo alias: clientes.id AS cliente_id
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
        cliente.setTipo(TipoCliente.valueOf(rs.getString("tipo_cliente")));

        // Verifica se há endereço associado (assumindo alias: enderecos.id AS endereco_id)
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
