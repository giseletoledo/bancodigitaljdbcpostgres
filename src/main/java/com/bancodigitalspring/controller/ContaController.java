package com.bancodigitalspring.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import com.bancodigitalspring.exception.BancoDadosException;
import com.bancodigitalspring.exception.ContaNaoEncontradaException;
import com.bancodigitalspring.model.Conta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bancodigitalspring.dto.ContaDTO;
import com.bancodigitalspring.dto.DepositoDTO;
import com.bancodigitalspring.dto.TransferenciaDTO;
import com.bancodigitalspring.dto.ValorRequest;
import com.bancodigitalspring.service.ContaService;

@RestController
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    // POST /contas - Cria uma nova conta
    @PostMapping
    public ResponseEntity<Void> criarConta(@RequestBody ContaDTO dto) throws SQLException {
        contaService.criarConta(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // GET /contas/{id} - Obter detalhes de uma conta
    @GetMapping("/{id}")
    public ResponseEntity<ContaDTO> buscarConta(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(contaService.buscarContaPorId(id));
    }

    // GET /contas/{id}/saldo - Consultar saldo
    @GetMapping("/{id}/saldo")
    public ResponseEntity<BigDecimal> consultarSaldo(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(contaService.consultarSaldo(id));
    }
    
 // GET /contas/{id}/extrato - Gerar extrato da conta
    @GetMapping("/{id}/extrato")
    public ResponseEntity<String> gerarExtrato(@PathVariable Long id) throws SQLException {
        String extrato = contaService.gerarExtrato(id);
        return ResponseEntity.ok(extrato);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Conta>> buscarContasPorClienteId(@PathVariable Long clienteId) {
        try {
            List<Conta> contas = contaService.buscarContasPorClienteId(clienteId);
            return ResponseEntity.ok(contas);
        } catch (BancoDadosException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/clientes/{id}/extratos")
    public ResponseEntity<String> extratosCliente(@PathVariable Long id) throws SQLException {
        String extratos = contaService.gerarExtratosCliente(id);
        return ResponseEntity.ok(extratos);
    }

    @PostMapping("/{id}/deposito")
    public ResponseEntity<Void> depositar(@PathVariable Long id, @RequestBody DepositoDTO request) throws SQLException {
        contaService.depositar(id, request.valor());
        return ResponseEntity.ok().build();
    }

    // POST /contas/{id}/saque - Realizar saque
    @PostMapping("/{id}/saque")
    public ResponseEntity<Void> sacar(@PathVariable Long id, @RequestBody ValorRequest request) throws SQLException {
        contaService.sacar(id, request.valor());
        return ResponseEntity.ok().build();
    }

    // POST /contas/{id}/transferencia - Transferência para outra conta
    @PostMapping("/{id}/transferencia")
    public ResponseEntity<Void> transferir(@PathVariable Long id, @RequestBody TransferenciaDTO dto) throws SQLException {
        contaService.transferir(id, dto.destinoId(), dto.valor());
        return ResponseEntity.ok().build();
    }
    
    // POST /contas/{id}/pix - Pagamento via Pix
    @PostMapping("/{id}/pix")
    public ResponseEntity<Void> pagarPix(@PathVariable Long id, @RequestBody PixRequest request) throws SQLException {
        contaService.realizarPix(id, request.chavePix(), request.valor());
        return ResponseEntity.ok().build();
    }


    // PUT /contas/{id}/manutencao - Aplicar taxa de manutenção
    @PutMapping("/{id}/manutencao")
    public ResponseEntity<Void> aplicarManutencao(@PathVariable Long id) throws SQLException {
        contaService.aplicarTaxaManutencao(id);
        return ResponseEntity.ok().build();
    }

    // PUT /contas/{id}/rendimentos - Aplicar rendimento mensal (se quiser implementar também)
    @PutMapping("/{id}/rendimentos")
    public ResponseEntity<Void> aplicarRendimento(@PathVariable Long id) throws SQLException {
        contaService.aplicarRendimento(id);
        return ResponseEntity.ok().build();
    }

    // DELETE /contas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable Long id) throws SQLException {
        contaService.deletarConta(id); // Todas as exceções são tratadas pelo GlobalExceptionHandler
        return ResponseEntity.noContent().build();
    }
}
