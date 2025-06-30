package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;

    public void mineBlock() {
        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();
        for (Transaction transaction : pendingTransactions) {
            if (!transactionService.updateBalance(transaction)){
                System.out.println("Invalid transaction");
            }
        }
        Block block = calculateBlockHash();
        blockRepository.save(block);

    }

    public Block calculateBlockHash() {
        int nonce = 0;
        String timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();;
        String hash;
        do {
            nonce++;
            String dataToHash = nonce + timestamp;
            hash = sha256(dataToHash);
        } while (!hash.startsWith("0000"));

        return Block.builder()
                .blockHash(hash)
                .timestamp(timestamp)
                .nonce(nonce)
                .build();
    }

    public String ValidateBlock() {
        Block byId = blockRepository.findById(1L).orElseThrow(
                () -> new BadRequestException("Block not found"));
        String dataToHash = byId.getNonce() + byId.getTimestamp();
        System.out.println(byId.getTimestamp());
        System.out.println(byId.getNonce());
        return sha256(dataToHash);
    }


    public String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash SHA-256", e);
        }
    }
}
