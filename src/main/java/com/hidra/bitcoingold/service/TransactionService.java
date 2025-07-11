package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.*;
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
        if (sourceWallet.getUuid().equals(destinationWallet.getUuid())) {
            throw new BadRequestException("You can't send money to yourself");
        }
        if (amount.add(unspentBalance()).compareTo(sourceWallet.getBalance()) > 0) {
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

    public void newUserBonusTransaction(User user){
        Wallet bankWallet = walletService.getWallet(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"));
        Wallet userWallet = walletService.getWallet(user.getWalletId());
        Transaction transaction = Transaction.builder()
                .source(bankWallet)
                .destination(userWallet)
                .amount(BigDecimal.valueOf(50))
                .block(null)
                .status(TransactionStatus.PENDING)
                .build();
        transactionRepository.save(transaction);
    }

    public void createMinerTransaction(UUID destination) {
        Wallet bankWallet = walletService.getWallet(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"));
        Wallet wallet = walletService.getWallet(destination);
        List<Transaction> bySourceAndStatus = transactionRepository.findBySourceAndStatus(bankWallet, TransactionStatus.PENDING);
        BigDecimal unspentBalance = BigDecimal.ZERO;
        for (Transaction transaction : bySourceAndStatus) {
            unspentBalance = unspentBalance.add(transaction.getAmount());
        }
        if (unspentBalance.add(BigDecimal.valueOf(50)).compareTo(wallet.getBalance()) > 0) {
            return;
        }

        Transaction minerTransaction = Transaction.builder()
                .source(bankWallet)
                .destination(wallet)
                .amount(BigDecimal.valueOf(50))
                .block(null)
                .status(TransactionStatus.PENDING)
                .build();
        transactionRepository.save(minerTransaction);
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

    public BigDecimal unspentBalance() {
        List<Transaction> userTransactions = getUserPendingTransactions();
        BigDecimal unspentTransactions = BigDecimal.ZERO;
        for (Transaction transaction : userTransactions) {
            unspentTransactions = transaction.getAmount().add(unspentTransactions);
        }
        return unspentTransactions;
    }

    public void updateBalance(List<Transaction> transactions, Block transactionBlock) {
        for (Transaction transaction : transactions) {
            Wallet source = transaction.getSource();
            Wallet destination = transaction.getDestination();
            if (source.getBalance().compareTo(transaction.getAmount()) < 0) {
                transaction.setStatus(TransactionStatus.INVALID);
                transactionRepository.save(transaction);
                System.out.println("Invalid transaction");
                continue;
            }
            source.setBalance(source.getBalance().subtract(transaction.getAmount()));
            destination.setBalance(destination.getBalance().add(transaction.getAmount()));
            walletRepository.save(source);
            walletRepository.save(destination);
            transaction.setStatus(TransactionStatus.MINED);
            transaction.setBlock(transactionBlock);
            transactionRepository.save(transaction);
        }
    }

    public Transaction pickMinerTransaction(List<Transaction> transactions) {
        for (Transaction transaction : transactions) {
            if (transaction.getAmount().compareTo(new BigDecimal("10")) >= 0) {
                if (!transaction.getSource().getUuid().equals(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"))) {
                    return transaction;
                }
            }
        }
        return null;
    }

    public List<Transaction> getTransactionsByBlock(Block block){
        return transactionRepository.findByBlock(block);
    }
}
