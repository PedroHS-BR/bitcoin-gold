package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.*;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private WalletRepository walletRepository;
    @Mock
    private UserService userService;
    @Mock
    private WalletService walletService;

    private Wallet sourceWallet;
    private Wallet destWallet;
    private User user;

    @BeforeEach
    void setUp() {
        sourceWallet = new Wallet(UUID.randomUUID(), new BigDecimal("100"));
        destWallet = new Wallet(UUID.randomUUID(), new BigDecimal("50"));
        user = User.builder().walletId(sourceWallet.getUuid().toString()).build();
    }

    @Test
    void createTransaction_shouldSucceed() {
        when(userService.getRegularUser()).thenReturn(user);
        when(walletService.getWallet(user.getWalletId())).thenReturn(sourceWallet);
        when(walletService.getWallet(destWallet.getUuid())).thenReturn(destWallet);

        BigDecimal amount = new BigDecimal("20");
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> i.getArgument(0));

        Transaction transaction = transactionService.createTransaction(destWallet.getUuid(), amount);

        assertEquals(sourceWallet, transaction.getSource());
        assertEquals(destWallet, transaction.getDestination());
        assertEquals(amount, transaction.getAmount());
        assertEquals(TransactionStatus.PENDING, transaction.getStatus());
    }

    @Test
    void createTransaction_shouldThrowForNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> transactionService.createTransaction(destWallet.getUuid(), new BigDecimal("-10")));
    }

    @Test
    void createTransaction_shouldThrowIfSendingToSelf() {
        when(userService.getRegularUser()).thenReturn(user);
        when(walletService.getWallet(user.getWalletId())).thenReturn(sourceWallet);
        when(walletService.getWallet(destWallet.getUuid())).thenReturn(sourceWallet);
        assertThrows(BadRequestException.class,
                () -> transactionService.createTransaction(destWallet.getUuid(), BigDecimal.TEN));
    }

    @Test
    void createTransaction_shouldThrowIfNotEnoughBalance() {
        TransactionService spyService = Mockito.spy(new TransactionService(
                transactionRepository, userService, walletService, walletRepository
        ));

        when(userService.getRegularUser()).thenReturn(user);
        when(walletService.getWallet(user.getWalletId())).thenReturn(sourceWallet);
        when(walletService.getWallet(destWallet.getUuid())).thenReturn(destWallet);


        sourceWallet.setBalance(new BigDecimal("100"));
        doReturn(BigDecimal.valueOf(90)).when(spyService).unspentBalance();

        assertThrows(BadRequestException.class,
                () -> spyService.createTransaction(destWallet.getUuid(), new BigDecimal("20")));
    }

    @Test
    void newUserBonusTransaction_shouldCreateBonus() {
        Wallet bankWallet = new Wallet(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"), new BigDecimal("1000"));
        when(walletService.getWallet(bankWallet.getUuid())).thenReturn(bankWallet);
        when(walletService.getWallet(user.getWalletId())).thenReturn(sourceWallet);

        transactionService.newUserBonusTransaction(user);

        verify(transactionRepository).save(argThat(t ->
                t.getSource().equals(bankWallet) &&
                        t.getDestination().equals(sourceWallet) &&
                        t.getAmount().equals(BigDecimal.valueOf(50))
        ));
    }

    @Test
    void createMinerTransaction_shouldCreateTransaction() {
        Wallet bankWallet = new Wallet(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"), new BigDecimal("1000"));
        Wallet minerWallet = new Wallet(UUID.randomUUID(), new BigDecimal("0"));

        when(walletService.getWallet(bankWallet.getUuid())).thenReturn(bankWallet);
        when(walletService.getWallet(minerWallet.getUuid())).thenReturn(minerWallet);
        when(transactionRepository.findBySourceAndStatus(bankWallet, TransactionStatus.PENDING))
                .thenReturn(Collections.emptyList());

        transactionService.createMinerTransaction(minerWallet.getUuid());

        verify(transactionRepository).save(argThat(t ->
                t.getSource().equals(bankWallet) &&
                        t.getDestination().equals(minerWallet) &&
                        t.getAmount().equals(BigDecimal.valueOf(50))
        ));
    }

    @Test
    void unspentBalance_shouldReturnSumOfPendingTransactions() {
        Transaction t1 = Transaction.builder().amount(new BigDecimal("30")).build();
        Transaction t2 = Transaction.builder().amount(new BigDecimal("70")).build();
        List<Transaction> mockPendingTransactions = List.of(t1, t2);

        TransactionService spyService = Mockito.spy(new TransactionService(
                transactionRepository, userService, walletService, walletRepository
        ));

        doReturn(mockPendingTransactions).when(spyService).getUserPendingTransactions();

        BigDecimal result = spyService.unspentBalance();

        assertEquals(new BigDecimal("100"), result);
    }

    @Test
    void updateBalance_shouldMarkInvalidIfInsufficientFunds() {
        Transaction t = Transaction.builder()
                .source(sourceWallet)
                .destination(destWallet)
                .amount(new BigDecimal("200"))
                .build();
        sourceWallet.setBalance(new BigDecimal("100"));

        transactionService.updateBalance(List.of(t), new Block());

        assertEquals(TransactionStatus.INVALID, t.getStatus());
        verify(transactionRepository).save(t);
    }

    @Test
    void updateBalance_shouldProcessValidTransaction() {
        Transaction t = Transaction.builder()
                .source(sourceWallet)
                .destination(destWallet)
                .amount(new BigDecimal("50"))
                .build();
        Block block = new Block();
        sourceWallet.setBalance(new BigDecimal("100"));
        destWallet.setBalance(new BigDecimal("0"));

        transactionService.updateBalance(List.of(t), block);

        assertEquals(TransactionStatus.MINED, t.getStatus());
        assertEquals(new BigDecimal("50"), destWallet.getBalance());
        assertEquals(new BigDecimal("50"), sourceWallet.getBalance());
        assertEquals(block, t.getBlock());
        verify(walletRepository, times(1)).save(sourceWallet);
        verify(walletRepository, times(1)).save(destWallet);
        verify(transactionRepository).save(t);
    }

    @Test
    void pickMinerTransaction_shouldReturnValid() {
        Wallet wallet = new Wallet(UUID.randomUUID(), BigDecimal.TEN);
        Wallet bank = new Wallet(UUID.fromString("84741a3d-ff44-45fe-af84-fe9e05079ef8"), BigDecimal.ZERO);
        Transaction t1 = Transaction.builder().amount(new BigDecimal("5")).source(wallet).build();
        Transaction t2 = Transaction.builder().amount(new BigDecimal("20")).source(wallet).build();
        Transaction t3 = Transaction.builder().amount(new BigDecimal("15")).source(bank).build();

        Transaction result = transactionService.pickMinerTransaction(List.of(t1, t2, t3));
        assertEquals(t2, result);
    }
}
