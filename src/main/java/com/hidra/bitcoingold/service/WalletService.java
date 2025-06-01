package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final AESUtil aesUtil;

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
}
