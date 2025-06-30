package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public void mineBlock() {
        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();
        for (Transaction transaction : pendingTransactions) {
            if (!transactionService.updateBalance(transaction)){
                System.out.println("Invalid transaction");
            }
        }

    }
}
