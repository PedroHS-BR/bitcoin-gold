package com.hidra.bitcoingold.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class AESUtilTest {

    private AESUtil aesUtil;

    private final String secretKey = "1234567890123456"; // 16 bytes (128 bits)

    @BeforeEach
    void setUp() {
        aesUtil = new AESUtil();
        String testKey = "1234567890123456";
        ReflectionTestUtils.setField(aesUtil, "secretKeyString", testKey);
        aesUtil.init();
    }

    @Test
    void shouldEncryptAndDecryptSuccessfully() throws Exception {
        String originalText = "texto-sensivel-123";

        String encrypted = aesUtil.encrypt(originalText);
        assertNotNull(encrypted);
        assertNotEquals(originalText, encrypted);

        String decrypted = aesUtil.decrypt(encrypted);
        assertEquals(originalText, decrypted);
    }

    @Test
    void shouldThrowExceptionOnInvalidDecryption() {
        String invalidEncrypted = "dado-invalido";

        Exception exception = assertThrows(Exception.class, () -> {
            aesUtil.decrypt(invalidEncrypted);
        });
        assertNotNull(exception.getMessage());
    }
}