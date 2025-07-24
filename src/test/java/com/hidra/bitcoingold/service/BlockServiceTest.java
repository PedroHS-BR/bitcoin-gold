package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.*;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockServiceTest {

    @InjectMocks
    private BlockService blockService;

    @Mock
    private BlockRepository blockRepository;
    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private WalletService walletService;

    private Wallet bankWallet;

    @BeforeEach
    void setup() {
        bankWallet = Wallet.builder()
                .uuid(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"))
                .balance(new BigDecimal("1000000"))
                .build();
    }

    @Test
    void isBlockchainEmpty_shouldReturnTrueWhenNoBlocksExist() {
        when(blockRepository.count()).thenReturn(0L);
        assertTrue(blockService.isBlockchainEmpty());
    }

    @Test
    void isBlockchainEmpty_shouldReturnFalseWhenBlocksExist() {
        when(blockRepository.count()).thenReturn(5L);
        assertFalse(blockService.isBlockchainEmpty());
    }

    @Test
    void createGenesisBlock_shouldCreateBlockIfNoneExists() {
        when(blockRepository.findAll()).thenReturn(Collections.emptyList());

        blockService.createGenesisBlock();

        verify(walletRepository).save(argThat(wallet ->
                wallet.getUuid().toString().equals("84741a3d-ff44-45fe-af84-fe9e05079ef8") &&
                        wallet.getBalance().compareTo(BigDecimal.valueOf(1_000_000)) == 0));
        verify(blockRepository).save(any(Block.class));
    }

    @Test
    void createGenesisBlock_shouldThrowIfBlockAlreadyExists() {
        when(blockRepository.findAll()).thenReturn(List.of(new Block()));
        assertThrows(BadRequestException.class, () -> blockService.createGenesisBlock());
    }

    @Test
    void getBlock_shouldReturnBlockIfExists() {
        Block block = new Block();
        when(blockRepository.getBlockById(1L)).thenReturn(Optional.of(block));
        assertEquals(block, blockService.getBlock(1L));
    }

    @Test
    void getBlock_shouldThrowIfNotFound() {
        when(blockRepository.getBlockById(1L)).thenReturn(Optional.empty());
        assertThrows(BadRequestException.class, () -> blockService.getBlock(1L));
    }

    @Test
    void validateBlock_shouldReturnValidMessageIfCorrectHash() {
        Block block = Block.builder()
                .id(1L)
                .previousHash("abc")
                .timestamp(Instant.now().toString())
                .nonce(1)
                .miner(bankWallet)
                .build();

        List<Transaction> transactions = Collections.emptyList();
        String correctHash = blockService.sha256("abc" + "" + 1 + block.getTimestamp() + bankWallet.getUuid().toString());
        block.setBlockHash(correctHash);

        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(transactionService.getTransactionsByBlock(block)).thenReturn(transactions);

        String result = blockService.ValidateBlock(1L);

        assertTrue(result.contains("Valid block"));
    }

    @Test
    void validateBlock_shouldReturnInvalidMessageIfIncorrectHash() {
        Block block = Block.builder()
                .id(1L)
                .previousHash("abc")
                .timestamp(Instant.now().toString())
                .nonce(1)
                .miner(bankWallet)
                .blockHash("wrong-hash")
                .build();

        when(blockRepository.findById(1L)).thenReturn(Optional.of(block));
        when(transactionService.getTransactionsByBlock(block)).thenReturn(Collections.emptyList());

        String result = blockService.ValidateBlock(1L);
        assertTrue(result.contains("Invalid block"));
    }

    @Test
    void calculateTransactionsHash_shouldReturnExpectedHash() {
        UUID sourceId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID destId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        Wallet w1 = Wallet.builder().uuid(sourceId).build();
        Wallet w2 = Wallet.builder().uuid(destId).build();
        Transaction t = Transaction.builder()
                .id(1L)
                .source(w1)
                .destination(w2)
                .amount(BigDecimal.valueOf(10))
                .build();

        String expectedInput = "1" + sourceId + destId + "10";
        String expectedHash = blockService.sha256(expectedInput);

        String actualHash = blockService.calculateTransactionsHash(List.of(t));

        assertEquals(expectedHash, actualHash);
    }


    @Test
    void createBlock_shouldCreateNewBlockWithValidHash() {
        Block previousBlock = Block.builder().blockHash("prevHash").build();
        Transaction tx1 = Transaction.builder()
                .id(1L)
                .source(bankWallet)
                .destination(Wallet.builder().uuid(UUID.randomUUID()).build())
                .amount(BigDecimal.valueOf(20))
                .build();

        List<Transaction> txList = List.of(tx1);

        when(blockRepository.findTopByOrderByIdDesc()).thenReturn(Optional.of(previousBlock));
        when(transactionService.getPendingTransactions()).thenReturn(txList);
        when(transactionService.pickMinerTransaction(txList)).thenReturn(tx1);
        when(walletService.getWallet(bankWallet.getUuid())).thenReturn(bankWallet);
        when(blockRepository.save(any(Block.class))).thenAnswer(i -> i.getArgument(0));

        Block newBlock = blockService.createBlock();

        assertNotNull(newBlock.getBlockHash());
        assertTrue(newBlock.getBlockHash().startsWith("0000"));
        assertEquals("prevHash", newBlock.getPreviousHash());
        verify(transactionService).updateBalance(txList, newBlock);
    }
}
