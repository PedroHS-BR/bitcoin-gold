package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.wallet.CreateTransactionRequest;
import com.hidra.bitcoingold.dtos.wallet.TransactionResponse;
import com.hidra.bitcoingold.mapper.TransactionMapper;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request) {
        Wallet wallet = walletService.getWalletByEmail(request.destination());
        Transaction transaction = transactionService.createTransaction(wallet.getUuid(), request.amount());
        TransactionResponse transactionResponse = TransactionMapper.toTransactionResponse(transaction);
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransaction() {
        List<Transaction> all = transactionService.findAll();
        List<TransactionResponse> transactionResponseList = TransactionMapper.toTransactionResponseList(all);
        return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<TransactionResponse>> getUserTransactions() {
        List<Transaction> userTransactions = transactionService.getUserTransactions();
        List<TransactionResponse> transactionResponseList = TransactionMapper.toTransactionResponseList(userTransactions);
        return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<TransactionResponse>> getPendingTransactions() {
        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();
        List<TransactionResponse> transactionResponseList = TransactionMapper.toTransactionResponseList(pendingTransactions);
        return new ResponseEntity<>(transactionResponseList, HttpStatus.OK);
    }
}
