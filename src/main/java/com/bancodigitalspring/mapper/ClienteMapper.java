package com.bancodigitalspring.mapper;

import com.bancodigitalspring.dto.ClienteDTO;
import com.bancodigitalspring.dto.EnderecoDTO;
import com.bancodigitalspring.model.Cliente;
import com.bancodigitalspring.model.Endereco;
import com.bancodigitalspring.model.TipoCliente;

public class ClienteMapper {

    public static ClienteDTO toDTO(Cliente cliente) {
        if (cliente == null) return null;

        Endereco endereco = cliente.getEndereco();

        EnderecoDTO enderecoDTO = new EnderecoDTO(
                endereco.getRua(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getCidade(),
                endereco.getEstado(),
                endereco.getCep()
        );

        return new ClienteDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getDataNascimento(), // Já é LocalDate
                enderecoDTO,
                cliente.getTipo().name()
        );
    }

    public static Cliente toEntity(ClienteDTO dto) {
        if (dto == null) return null;

        EnderecoDTO enderecoDTO = dto.endereco();

        Endereco endereco = new Endereco(
                enderecoDTO.rua(),
                enderecoDTO.numero(),
                enderecoDTO.complemento(),
                enderecoDTO.cidade(),
                enderecoDTO.estado(),
                enderecoDTO.cep()
        );

        TipoCliente tipo;
        try {
            tipo = TipoCliente.valueOf(dto.tipoCliente().toUpperCase());
        } catch (Exception e) {
            tipo = TipoCliente.COMUM;
        }

        Cliente cliente = new Cliente(
                dto.nome(),
                dto.cpf(),
                dto.dataNascimento(),
                endereco,
                tipo
        );

        if (dto.id() != null) {
            cliente.setId(dto.id());
        }

        return cliente;
    }
}