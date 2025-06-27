package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AESUtil aesUtil;
    private final UserRepository userRepository;

    public String createWallet()  {
        Wallet wallet = new Wallet();
        String encrypt = null;
        try {
            encrypt = aesUtil.encrypt(wallet.getUuid().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        walletRepository.save(wallet);

        return encrypt;
    }

    public Wallet getWallet(String walletId) {
        try {
            String decrypt = aesUtil.decrypt(walletId);
            return walletRepository.findById(UUID.fromString(decrypt))
                    .orElseThrow(() -> new BadRequestException("Wallet not found"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Wallet getWalletByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("User not found"));
        return getWallet(user.getWalletId());
    }

    public Wallet getWallet(UUID uuid) {
        return walletRepository.findById(uuid).orElseThrow(() -> new BadRequestException("Wallet not found"));
    }

    public void deleteWallet(String walletId) {
        Wallet wallet = getWallet(walletId);
        walletRepository.delete(wallet);
    }

    public void updateBalance(Wallet source, Wallet destination, BigDecimal amount) {
        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Source balance must be greater than or equal to transaction value");
        }
        source.setBalance(source.getBalance().subtract(amount));
        destination.setBalance(destination.getBalance().add(amount));
        walletRepository.save(source);
        walletRepository.save(destination);
    }
}
