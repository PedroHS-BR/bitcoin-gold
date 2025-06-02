package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.wallet.CreateTransactionRequest;
import com.hidra.bitcoingold.dtos.wallet.TransactionResponse;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest request) {
        System.out.println("Entrou");
        Wallet wallet = walletService.getWallet(request.destination());
        Transaction transaction = transactionService.createTransaction(wallet.getUuid(), request.amount());
        TransactionResponse transactionResponse = new TransactionResponse(transaction.getId(),
                transaction.getSource().getUuid(),
                transaction.getDestination().getUuid(),
                transaction.getAmount());
        return new ResponseEntity<>(transactionResponse, HttpStatus.CREATED);
    }
}
