package com.bancodigitalspring.dao;

import com.bancodigitalspring.config.DatabaseConfig;
import com.bancodigitalspring.mapper.ClienteMapper;
import com.bancodigitalspring.mapper.ClienteMapperDB;
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

    public Cliente buscarClientePorId(Long id) throws SQLException {
        String sql = "SELECT cliente_id, nome, cpf, data_nascimento, tipo_cliente, " +
                "endereco_id, rua, numero, complemento, cidade, estado, cep " +
                "FROM buscar_cliente_por_id(?)";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return ClienteMapperDB.mapearCliente(rs);
                } else {
                    return null;
                }
            }
        }
    }

    public List<Cliente> listarClientes() throws SQLException {

        String sql = """
        	    SELECT c.id AS cliente_id, c.nome, c.cpf, c.data_nascimento, c.tipo_cliente,
        	           e.id AS endereco_id, e.cliente_id AS endereco_cliente_id,
        	           e.rua, e.numero, e.complemento, e.cidade, e.estado, e.cep
        	    FROM clientes c
        	    LEFT JOIN enderecos e ON c.id = e.cliente_id
        	""";

        List<Cliente> clientes = new ArrayList<>();

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = ClienteMapperDB.mapearCliente(rs);
                clientes.add(cliente);
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
}
