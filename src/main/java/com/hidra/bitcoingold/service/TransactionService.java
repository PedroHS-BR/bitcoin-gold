package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

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
        walletService.updateBalance(sourceWallet, destinationWallet, amount);
        Transaction transaction = Transaction.builder()
                .source(sourceWallet)
                .destination(destinationWallet)
                .amount(amount)
                .build();
        return transactionRepository.save(transaction);
    }

}
