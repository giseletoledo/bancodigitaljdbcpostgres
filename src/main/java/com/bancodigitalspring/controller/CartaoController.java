package com.bancodigitalspring.controller;

import java.util.List;
import java.util.Map;

import com.bancodigitalspring.dto.CartaoAtualizacaoDTO;
import com.bancodigitalspring.dto.CartaoDTO;
import com.bancodigitalspring.dto.CartaoPagamentoDTO;
import com.bancodigitalspring.model.Cartao;
import com.bancodigitalspring.model.CartaoTransacao;
import com.bancodigitalspring.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {

    @Autowired
    private CartaoService cartaoService;

    // POST /cartoes - Criar cartão
    @PostMapping
    public ResponseEntity<String> criarCartao(@RequestBody CartaoDTO dto) {
        try {
            cartaoService.criarCartao(dto);
            return ResponseEntity.ok("Cartão criado com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar cartão: " + e.getMessage());
        }
    }

    // GET /cartoes/{id} - Detalhes do cartão
    @GetMapping("/{id}")
    public ResponseEntity<Cartao> buscarCartao(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cartaoService.buscarCartao(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // POST /cartoes/{id}/pagamento - Usar cartão
    @PostMapping("/{id}/pagamento")
    public ResponseEntity<Void> realizarPagamento(@PathVariable Long id, @RequestBody CartaoPagamentoDTO dto) {
        try {
            cartaoService.registrarPagamento(id, dto.valor(), dto.descricao());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /cartoes/{id}/limite - Alterar limite
    @PutMapping("/{id}/limite")
    public ResponseEntity<Void> atualizarLimite(@PathVariable Long id, @RequestBody CartaoAtualizacaoDTO dto) {
        try {
            cartaoService.alterarLimite(id, dto.limite());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /cartoes/{id}/status - Ativar/Desativar
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            cartaoService.alterarStatus(id, body.get("status"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /cartoes/{id}/senha - Alterar senha
    @PutMapping("/{id}/senha")
    public ResponseEntity<Void> atualizarSenha(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            cartaoService.alterarSenha(id, body.get("senha"));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // PUT /cartoes/{id}/limite-diario - Débito: Atualizar limite diário
    @PutMapping("/{id}/limite-diario")
    public ResponseEntity<Void> atualizarLimiteDiario(@PathVariable Long id, @RequestBody CartaoAtualizacaoDTO dto) {
        try {
            cartaoService.alterarLimiteDiario(id, dto.limiteDiario());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET /cartoes/{id}/fatura - Consulta de fatura (simplificado)
    @GetMapping("/{id}/fatura")
    public ResponseEntity<List<CartaoTransacao>> consultarFatura(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cartaoService.consultarFatura(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
