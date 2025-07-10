package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockService {

    private final BlockRepository blockRepository;
    private final TransactionService transactionService;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final WalletService walletService;

    public boolean isBlockchainEmpty() {
        return blockRepository.count() == 0;
    }

    public String ValidateBlock(Long blockId) {
        Block block = blockRepository.findById(blockId).orElseThrow(
                () -> new BadRequestException("Block not found"));

        String previousHash = block.getPreviousHash();
        List<Transaction> transactions = transactionService.getTransactionsByBlock(block);
        String transactionsHash = transactions.isEmpty() ? "" : calculateTransactionsHash(transactions);

        long nonce = block.getNonce();
        String timestamp = block.getTimestamp();
        Wallet miner = block.getMiner();

        String s = sha256(previousHash + transactionsHash + nonce + timestamp + miner.getUuid().toString());
        if (s.equals(block.getBlockHash())) return "Valid block, Block hash: " + s;
        else return "Invalid block, generated hash: " + s;
    }

    public Block createBlock() {
        Block previousBlock = blockRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new BadRequestException("Block not found"));

        List<Transaction> transactions = transactionService.getPendingTransactions();
        Transaction minerTransaction = transactionService.pickMinerTransaction(transactions);
        String miner;
        if (minerTransaction != null) {
            Wallet source = minerTransaction.getSource();
            miner = source.getUuid().toString();
        }
        else miner = "84741a3d-ff44-45fe-af84-fe9e05079ef8";


        String previousHash = previousBlock.getBlockHash();
        String transactionsHash = transactions.isEmpty() ? "" : calculateTransactionsHash(transactions);
        String timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();

        String hash;
        int nonce = 0;
        do {
            nonce++;
            hash = sha256(previousHash + transactionsHash + nonce + timestamp + miner);
        } while (!hash.startsWith("0000"));

        Block createdBlock = blockRepository.save(Block.builder()
                .blockHash(hash)
                .previousHash(previousHash)
                .transactionHash(transactionsHash)
                .miner(walletService.getWallet(UUID.fromString(miner)))
                .timestamp(timestamp)
                .nonce(nonce)
                .build());
        transactionService.updateBalance(transactions, createdBlock);
        return createdBlock;
    }

    public void createGenesisBlock() {
        List<Block> all = blockRepository.findAll();
        UUID bankId = UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8");
        Wallet bankWallet = Wallet.builder()
                .uuid(bankId)
                .balance(BigDecimal.valueOf(1_000_000))
                .build();
        walletRepository.save(bankWallet);
        if (!all.isEmpty()) {
            throw new BadRequestException("Block already exists");
        }
        String timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();
        String miner = bankWallet.getUuid().toString();
        String hash;
        int nonce = 0;
        do {
            nonce++;
            hash = sha256(nonce + timestamp + miner);
        } while (!hash.startsWith("0000"));

        Block build = Block.builder()
                .blockHash(hash)
                .previousHash("")
                .transactionHash("")
                .miner(bankWallet)
                .timestamp(timestamp)
                .nonce(nonce)
                .build();
        blockRepository.save(build);
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

    public Block getBlock(long id) {
        return blockRepository.getBlockById(id).orElseThrow(() -> new BadRequestException("Block not found"));
    }
}
