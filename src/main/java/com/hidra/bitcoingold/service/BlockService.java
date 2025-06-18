package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.repository.BlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final TransactionService transactionService;

    public void mineBlock(String blockHash) {

        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();

    }
}
