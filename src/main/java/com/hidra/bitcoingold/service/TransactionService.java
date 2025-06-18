package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.wallet.TransactionRequest;
import com.hidra.bitcoingold.dtos.wallet.TransactionResponse;
import com.hidra.bitcoingold.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    public Transaction createTransaction(UUID destination, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Wallet destinationWallet = walletService.getWallet(destination);
        User user = userService.getRegularUser();
        Wallet sourceWallet = walletService.getWallet(user.getWalletId());
        //walletService.updateBalance(sourceWallet, destinationWallet, amount);
        Transaction transaction = Transaction.builder()
                .source(sourceWallet)
                .destination(destinationWallet)
                .amount(amount)
                .block(null)
                .status(TransactionStatus.PENDING)
                .build();
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getPendingTransactions() {
        return transactionRepository.findTop100ByStatus(TransactionStatus.PENDING);
    }


    public boolean validateTransactionsData(List<TransactionRequest> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return false;
        }
        List<Long> ids = transactions.stream()
                .map(TransactionRequest::id)
                .collect(Collectors.toList());
        List<Transaction> transactionsFromDb = transactionRepository.findPendingWithValidWalletsByIds(ids);
        if (transactionsFromDb.size() != transactions.size()) {
            return false;
        }
        Map<Long, Transaction> mapDb = transactionsFromDb.stream()
                .collect(Collectors.toMap(Transaction::getId, t -> t));
        for (TransactionRequest tx : transactions) {
            Transaction txDb = mapDb.get(tx.id());
            if (txDb == null) {
                return false;
            }
            if (!tx.source().equals(txDb.getSource().getUuid())) {
                return false;
            }
            if (!tx.destination().equals(txDb.getDestination().getUuid())) {
                return false;
            }
            if (tx.amount() == null || txDb.getAmount() == null || tx.amount().compareTo(txDb.getAmount()) != 0) {
                return false;
            }
        }

        return true;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }
}
