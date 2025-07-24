package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private AESUtil aesUtil;

    @Mock
    private UserRepository userRepository;



    @Test
    void createWallet_shouldEncryptUuidAndSaveWallet() throws Exception {
        String encryptedUuid = "encrypted-id";

        when(aesUtil.encrypt(any())).thenReturn(encryptedUuid);

        String result = walletService.createWallet();

        assertEquals(encryptedUuid, result);
        verify(walletRepository).save(any(Wallet.class));
        verify(aesUtil).encrypt(any());
    }

    @Test
    void getWallet_shouldDecryptAndReturnWallet() throws Exception {
        UUID uuid = UUID.randomUUID();
        String encrypted = "encrypted-id";
        String decrypted = uuid.toString();
        Wallet wallet = new Wallet();
        wallet.setUuid(uuid);

        when(aesUtil.decrypt(encrypted)).thenReturn(decrypted);
        when(walletRepository.findById(uuid)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWallet(encrypted);

        assertEquals(wallet, result);
        verify(aesUtil).decrypt(encrypted);
        verify(walletRepository).findById(uuid);
    }

    @Test
    void getWallet_shouldThrowWhenWalletNotFound() throws Exception {
        UUID uuid = UUID.randomUUID();
        String encrypted = "encrypted-id";
        String decrypted = uuid.toString();

        when(aesUtil.decrypt(encrypted)).thenReturn(decrypted);
        when(walletRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> walletService.getWallet(encrypted));
    }

    @Test
    void getWalletByEmail_shouldReturnWallet() throws Exception {
        String email = "test@example.com";
        UUID walletUuid = UUID.randomUUID();
        String encryptedWalletId = "encrypted-wallet-id";

        User user = new User();
        user.setEmail(email);
        user.setWalletId(encryptedWalletId);

        Wallet wallet = new Wallet();
        wallet.setUuid(walletUuid);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(aesUtil.decrypt(encryptedWalletId)).thenReturn(walletUuid.toString());
        when(walletRepository.findById(walletUuid)).thenReturn(Optional.of(wallet));

        Wallet result = walletService.getWalletByEmail(email);

        assertEquals(wallet, result);
    }

    @Test
    void updateBalance_shouldUpdateWalletsIfSufficientBalance() {
        Wallet source = new Wallet();
        source.setBalance(BigDecimal.valueOf(100));

        Wallet destination = new Wallet();
        destination.setBalance(BigDecimal.valueOf(50));

        BigDecimal amount = BigDecimal.valueOf(30);

        walletService.updateBalance(source, destination, amount);

        assertEquals(BigDecimal.valueOf(70), source.getBalance());
        assertEquals(BigDecimal.valueOf(80), destination.getBalance());

        verify(walletRepository).save(source);
        verify(walletRepository).save(destination);
    }

    @Test
    void updateBalance_shouldThrowIfInsufficientBalance() {
        Wallet source = new Wallet();
        source.setBalance(BigDecimal.valueOf(20));

        Wallet destination = new Wallet();
        destination.setBalance(BigDecimal.valueOf(50));

        BigDecimal amount = BigDecimal.valueOf(30);

        assertThrows(IllegalArgumentException.class, () ->
                walletService.updateBalance(source, destination, amount));
    }
}
