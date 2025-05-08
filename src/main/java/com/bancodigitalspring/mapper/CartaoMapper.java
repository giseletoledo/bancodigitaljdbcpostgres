package com.bancodigitalspring.mapper;

import com.bancodigitalspring.dto.CartaoDTO;
import com.bancodigitalspring.model.Cartao;

public class CartaoMapper {

    public static CartaoDTO toDTO(Cartao cartao) {
        return new CartaoDTO(
            cartao.getId(),
            cartao.getContaId(),
            cartao.getNumeroCartao(),
            cartao.getTipoCartao(), 
            cartao.getLimite(),
            cartao.getLimiteDiario(),
            cartao.isAtivo(),
            cartao.getSenha()
        );
    }

    public static Cartao fromDTO(CartaoDTO dto) {
        Cartao cartao = new Cartao();
        cartao.setId(dto.id());
        cartao.setContaId(dto.contaId());
        cartao.setNumeroCartao(dto.numeroCartao());
        cartao.setTipoCartao(dto.tipoCartao()); 
        cartao.setLimite(dto.limite());
        cartao.setLimiteDiario(dto.limiteDiario());
        cartao.setAtivo(dto.ativo());
        cartao.setSenha(dto.senha());
        return cartao;
    }
}
