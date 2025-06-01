package com.hidra.bitcoingold.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AESUtil {

    private static final String AES = "AES";
    private static final String AES_MODE = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    private SecretKey secretKey;

    @Value("${crypto.key}")
    private String secretKeyString;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secretKeyString.getBytes();
        this.secretKey = new SecretKeySpec(keyBytes, AES);
    }

    public String encrypt(String plainText) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());
        byte[] encrypted = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(cipherText, 0, encrypted, iv.length, cipherText.length);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String encryptedBase64) throws Exception {
        byte[] encrypted = Base64.getDecoder().decode(encryptedBase64);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(encrypted, 0, iv, 0, iv.length);
        byte[] cipherText = new byte[encrypted.length - iv.length];
        System.arraycopy(encrypted, iv.length, cipherText, 0, cipherText.length);
        Cipher cipher = Cipher.getInstance(AES_MODE);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);
    }
}
