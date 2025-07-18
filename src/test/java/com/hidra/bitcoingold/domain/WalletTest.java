package com.hidra.bitcoingold.domain;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void emptyConstructor_CreateValidWallet() {
        Wallet wallet = new Wallet();
        assertNotNull(wallet);
        assertNotNull(wallet.getUuid());
        assertEquals(BigDecimal.ZERO, wallet.getBalance());
    }
}