package com.bancodigitalspring.controller;

import java.sql.SQLException;
import java.util.List;

import com.bancodigitalspring.dto.ClienteDTO;
import com.bancodigitalspring.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
	
	private final ClienteService clienteService;

	@Autowired
	public ClienteController(ClienteService clienteService) {
		this.clienteService = clienteService;
	}


    // Criar cliente (POST)
    @PostMapping
    public String criarCliente(@RequestBody ClienteDTO clientedto) {
        try {
        	clienteService.criarCliente(clientedto);
            return "Cliente criado com sucesso!";
        } catch (SQLException e) {
            return "Erro ao criar cliente: " + e.getMessage();
        }
    }

    // Listar todos os clientes (GET)
    @GetMapping
    public List<ClienteDTO> listarClientes() throws SQLException {
        return clienteService.listarClientes();
    }

    // Buscar cliente por ID (GET)
    @GetMapping("/{id}")
    public ClienteDTO buscarClientePorId(@PathVariable Long id) throws SQLException {
        return clienteService.buscarClientePorId(id);
    }

 // Atualizar cliente (PUT)
    @PutMapping("/{id}")
    public String atualizarCliente(@PathVariable Long id, @RequestBody ClienteDTO clientedto) {
        try {
            clienteService.atualizarCliente(id, clientedto);
            return "Cliente atualizado com sucesso!";
        } catch (SQLException e) {
            return "Erro ao atualizar cliente: " + e.getMessage();
        } catch (IllegalArgumentException e) {
            return "Dados inválidos: " + e.getMessage();
        }
    }


    // Deletar cliente (DELETE)
    @DeleteMapping("/{id}")
    public String deletarCliente(@PathVariable Long id) {
        try {
        	clienteService.deletarCliente(id);
            return "Cliente removido com sucesso!";
        } catch (SQLException e) {
            return "Erro ao remover cliente: " + e.getMessage();
        }
    }
}
