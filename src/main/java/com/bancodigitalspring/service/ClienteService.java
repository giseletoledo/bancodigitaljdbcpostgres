package com.bancodigitalspring.service;

import com.bancodigitalspring.controller.ValidadorCliente;
import com.bancodigitalspring.dao.ClienteDAO;
import com.bancodigitalspring.dto.ClienteDTO;
import com.bancodigitalspring.mapper.ClienteMapper;
import com.bancodigitalspring.model.Cliente;
import com.bancodigitalspring.model.Endereco;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {
    

    private final ClienteDAO clienteDAO;

    @Autowired
    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void criarCliente(ClienteDTO dto) throws SQLException {
        Cliente cliente = ClienteMapper.toEntity(dto);
        validarCliente(cliente);
        clienteDAO.criarCliente(cliente);
    }

    public List<ClienteDTO> listarClientes() throws SQLException {
        return clienteDAO.listarClientes()
                .stream()
                .map(ClienteMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ClienteDTO buscarClientePorId(Long id) throws SQLException {
        Cliente cliente = clienteDAO.buscarClientePorId(id);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado.");
        }
        return ClienteMapper.toDTO(cliente); // aqui retorna ClienteDTO
    }


    public void atualizarCliente(Long id, ClienteDTO dto) throws SQLException {
        Cliente cliente = ClienteMapper.toEntity(dto);
        cliente.setId(id);
        validarCliente(cliente);
        clienteDAO.atualizarCliente(cliente);
    }


    public void deletarCliente(Long id) throws SQLException {
        clienteDAO.deletarCliente(id);
    }


    private void validarCliente(Cliente cliente) {
        if (!ValidadorCliente.validarNome(cliente.getNome())) {
            throw new IllegalArgumentException("Nome inválido. Deve conter apenas letras e ter entre 2 e 100 caracteres.");
        }

        if (!ValidadorCliente.validarCPF(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        if (!ValidadorCliente.validarDataNascimento(cliente.getDataNascimento())) {
            throw new IllegalArgumentException("Data de nascimento inválida ou cliente menor de 18 anos.");
        }

        if (cliente.getEndereco() == null) {
            throw new IllegalArgumentException("Endereço é obrigatório.");
        }

        validarEndereco(cliente.getEndereco());
    }

    private void validarEndereco(Endereco endereco) {
        if (endereco.getRua() == null || endereco.getRua().trim().isEmpty()) {
            throw new IllegalArgumentException("Rua é obrigatória.");
        }

        if (endereco.getNumero() == null || endereco.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("Número é obrigatório.");
        }

        if (endereco.getCidade() == null || endereco.getCidade().trim().isEmpty()) {
            throw new IllegalArgumentException("Cidade é obrigatória.");
        }

        if (endereco.getEstado() == null || endereco.getEstado().length() != 2) {
            throw new IllegalArgumentException("Estado deve ter exatamente 2 letras.");
        }

        if (!ValidadorCliente.validarCEP(endereco.getCep())) {
            throw new IllegalArgumentException("CEP inválido. Use o formato 12345-678.");
        }
    }
}


