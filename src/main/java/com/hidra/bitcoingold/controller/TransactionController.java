package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.wallet.CreateTransactionRequest;
import com.hidra.bitcoingold.dtos.wallet.TransactionResponse;
import com.hidra.bitcoingold.mapper.TransactionMapper;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final WalletService walletService;

    @Operation(
            summary = "Criar uma nova transação entre carteiras",
            description = """
        Cria uma transação pendente da carteira do usuário autenticado para a carteira de destino informada pelo e-mail do seu dono.
        
        Regras aplicadas:
        - Deve ser informado o e-mail do dono da carteira de destino.
        - O valor da transação deve ser maior que zero.
        - Não é permitido transferir para a própria carteira.
        - O saldo disponível deve ser suficiente para cobrir o valor da transação.
        
        Retorna os dados da transação criada com status HTTP 201.
        """
    )
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request) {
        Wallet wallet = walletService.getWalletByEmail(request.destination());
        Transaction transaction = transactionService.createTransaction(wallet.getUuid(), request.amount());
        TransactionResponse transactionResponse = TransactionMapper.toTransactionResponse(transaction);
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Listar todas as transações",
            description = """
        Retorna uma lista com todas as transações registradas no sistema, incluindo as pendentes e as já mineradas.
        
        Disponível apenas para usuários autenticados.
        """
    )
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransaction() {
        List<Transaction> all = transactionService.findAll();
        List<TransactionResponse> transactionResponseList = TransactionMapper.toTransactionResponseList(all);
        return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
    }

    @Operation(
            summary = "Listar transações do usuário autenticado",
            description = """
        Retorna todas as transações em que a carteira do usuário autenticado foi a origem.
        
        Permite que o usuário acompanhe seu histórico de envios. Transações pendentes e mineradas podem ser incluídas.
        """
    )
    @GetMapping("/user")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions() {
        List<Transaction> userTransactions = transactionService.getUserTransactions();
        List<TransactionResponse> transactionResponseList = TransactionMapper.toTransactionResponseList(userTransactions);
        return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
    }

    @Operation(
            summary = "Listar transações pendentes",
            description = """
        Retorna até 100 transações com status PENDING (ainda não mineradas). 
        Essas transações aguardam inclusão em um bloco e podem ser mineradas a qualquer momento.
        """
    )
    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponse>> getPendingTransactions() {
        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();
        List<TransactionResponse> transactionResponseList = TransactionMapper.toTransactionResponseList(pendingTransactions);
        return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
    }
}
