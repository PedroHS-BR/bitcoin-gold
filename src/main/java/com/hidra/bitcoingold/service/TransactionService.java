package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;
    private final WalletRepository walletRepository;

    public Transaction createTransaction(UUID destination, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Wallet destinationWallet = walletService.getWallet(destination);
        User user = userService.getRegularUser();
        Wallet sourceWallet = walletService.getWallet(user.getWalletId());
        if (amount.add(unexpentBalance()).compareTo(sourceWallet.getBalance()) > 0) {
            throw new BadRequestException("You don't have enough balance");
        }
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

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getUserPendingTransactions() {
        User user = userService.getRegularUser();
        Wallet wallet = walletService.getWallet(user.getWalletId());
        return transactionRepository.findBySourceAndStatus(wallet, TransactionStatus.PENDING);
    }

    public List<Transaction> getUserTransactions() {
        User user = userService.getRegularUser();
        Wallet wallet = walletService.getWallet(user.getWalletId());
        return transactionRepository.findBySource(wallet);
    }

    public BigDecimal unexpentBalance() {
        List<Transaction> userTransactions = getUserPendingTransactions();
        BigDecimal unexpentTransactions = BigDecimal.ZERO;
        for (Transaction transaction : userTransactions) {
            unexpentTransactions = transaction.getAmount().add(unexpentTransactions);
        }
        return unexpentTransactions;
    }

    public boolean updateBalance(Transaction transaction) {
        Wallet source = transaction.getSource();
        Wallet destination = transaction.getDestination();
        if(source.getBalance().compareTo(transaction.getAmount()) < 0) {
            transaction.setStatus(TransactionStatus.INVALID);
            transactionRepository.save(transaction);
            return false;
        }
        source.setBalance(source.getBalance().subtract(transaction.getAmount()));
        destination.setBalance(destination.getBalance().add(transaction.getAmount()));
        walletRepository.save(source);
        walletRepository.save(destination);
        transaction.setStatus(TransactionStatus.MINED);
        transactionRepository.save(transaction);
        return true;
    }
}
