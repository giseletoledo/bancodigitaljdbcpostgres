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
    	 String sqlCliente = "INSERT INTO clientes (nome, cpf, data_nascimento, tipo_cliente) VALUES (?, ?, ?, ?)";
         String sqlEndereco = "INSERT INTO enderecos (cliente_id, rua, numero, complemento, cidade, estado, cep) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConfig.conectar();
                PreparedStatement stmtCliente = connection.prepareStatement(sqlCliente, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement stmtEndereco = connection.prepareStatement(sqlEndereco)) {

               // Inserir cliente
               stmtCliente.setString(1, cliente.getNome());
               stmtCliente.setString(2, cliente.getCpf());
               stmtCliente.setDate(3, Date.valueOf(cliente.getDataNascimento()));
               stmtCliente.setString(4, cliente.getTipo().name());
               stmtCliente.executeUpdate();

               // Obter ID gerado do cliente
               ResultSet generatedKeys = stmtCliente.getGeneratedKeys();
               if (generatedKeys.next()) {
                   long clienteId = generatedKeys.getLong(1);

                   // Inserir endereço
                   if (cliente.getEndereco() != null) {
                       stmtEndereco.setLong(1, clienteId);
                       stmtEndereco.setString(2, cliente.getEndereco().getRua());
                       stmtEndereco.setString(3, cliente.getEndereco().getNumero());
                       stmtEndereco.setString(4, cliente.getEndereco().getComplemento());
                       stmtEndereco.setString(5, cliente.getEndereco().getCidade());
                       stmtEndereco.setString(6, cliente.getEndereco().getEstado());
                       stmtEndereco.setString(7, cliente.getEndereco().getCep());
                       stmtEndereco.executeUpdate();
                   }
               }
           }
    }

    public Cliente buscarClientePorId(Long id) throws SQLException {
    	String sql = """
    		    SELECT c.id AS cliente_id, c.nome, c.cpf, c.data_nascimento, c.tipo_cliente,
    		           e.id AS endereco_id, e.cliente_id AS endereco_cliente_id,
    		           e.rua, e.numero, e.complemento, e.cidade, e.estado, e.cep
    		    FROM clientes c
    		    LEFT JOIN enderecos e ON c.id = e.cliente_id
    		    WHERE c.id = ?
    		""";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearCliente(rs);
            }
        }
        return null;
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
                Cliente cliente = mapearCliente(rs);
                clientes.add(cliente);
            }
        }
        return clientes;
    }


    public void atualizarCliente(Cliente cliente) throws SQLException {
        String sqlCliente = "UPDATE clientes SET nome = ?, data_nascimento = ?, tipo_cliente = ? WHERE id = ?";
        String sqlEndereco = "UPDATE enderecos SET rua = ?, numero = ?, complemento = ?, cidade = ?, estado = ?, cep = ? WHERE cliente_id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmtCliente = connection.prepareStatement(sqlCliente);
             PreparedStatement stmtEndereco = connection.prepareStatement(sqlEndereco)) {

            // Atualizar a tabela clientes
            stmtCliente.setString(1, cliente.getNome());
            stmtCliente.setDate(2, Date.valueOf(cliente.getDataNascimento()));
            stmtCliente.setString(3, cliente.getTipo().name());
            stmtCliente.setLong(4, cliente.getId());
            stmtCliente.executeUpdate();

            // Atualizar a tabela enderecos, se houver endereço
            if (cliente.getEndereco() != null) {
                stmtEndereco.setString(1, cliente.getEndereco().getRua());
                stmtEndereco.setString(2, cliente.getEndereco().getNumero());
                stmtEndereco.setString(3, cliente.getEndereco().getComplemento());
                stmtEndereco.setString(4, cliente.getEndereco().getCidade());
                stmtEndereco.setString(5, cliente.getEndereco().getEstado());
                stmtEndereco.setString(6, cliente.getEndereco().getCep());
                stmtEndereco.setLong(7, cliente.getId());
                stmtEndereco.executeUpdate();
            }
        }
    }


    public void deletarCliente(Long id) throws SQLException {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection connection = DatabaseConfig.conectar();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();
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
