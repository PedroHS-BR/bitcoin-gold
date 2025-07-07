package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.domain.Wallet;
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
        if (pendingTransactions.isEmpty()) {
            throw new BadRequestException("No pending transactions found");
        }
        Transaction MinerTransaction = transactionService.updateBalance(pendingTransactions);
        Block block = createBlock(pendingTransactions, MinerTransaction);
        blockRepository.save(block);

    }

    public Block createBlock(List<Transaction> transactions, Transaction minerTransaction) {
        Block previousBlock = blockRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new BadRequestException("Block not found"));

        if (minerTransaction != null) {
            Wallet source = minerTransaction.getSource();

        }

        String previousHash = previousBlock.getBlockHash();
        String TransactionsHash = transactions.isEmpty() ? "" : calculateTransactionsHash(transactions);
        String timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
        return null;
    }



    public Block createGenesisBlock() {
        List<Block> all = blockRepository.findAll();
        if (!all.isEmpty()) {
            throw new BadRequestException("Block already exists");
        }
        int nonce = 0;
        String timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
        String hash;
        do {
            nonce++;
            String dataToHash = nonce + timestamp;
            hash = sha256(dataToHash);
        } while (!hash.startsWith("0000"));

        Wallet genesisWallet = new Wallet();

        return Block.builder()
                .blockHash(hash)
                .timestamp(timestamp)
                .nonce(nonce)
                .build();
    }

    public String ValidateBlock(Long blockId) {
        Block byId = blockRepository.findById(blockId).orElseThrow(
                () -> new BadRequestException("Block not found"));
        String dataToHash = byId.getNonce() + byId.getTimestamp();

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

    public String calculateTransactionsHash(List<Transaction> transactions) {
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : transactions) {
            sb.append(tx.getId())
                    .append(tx.getSource().getUuid())
                    .append(tx.getDestination().getUuid())
                    .append(tx.getAmount().toPlainString());
        }
        return sha256(sb.toString());
    }
}
