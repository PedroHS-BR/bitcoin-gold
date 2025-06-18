package com.hidra.bitcoingold.mapper;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.dtos.wallet.TransactionResponse;

import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {

    public static TransactionResponse toTransactionResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(),
                transaction.getSource().getUuid(),
                transaction.getDestination().getUuid(),
                transaction.getAmount(),
                transaction.getStatus());
    }

    public static List<TransactionResponse> toTransactionResponseList(List<Transaction> transactions) {
        List<TransactionResponse> transactionResponseList = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionResponseList.add(toTransactionResponse(transaction));
        }
        return transactionResponseList;
    }
}
