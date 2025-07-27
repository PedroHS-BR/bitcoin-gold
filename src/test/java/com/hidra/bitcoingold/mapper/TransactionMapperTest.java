package com.hidra.bitcoingold.mapper;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.wallet.TransactionResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionMapperTest {

    @Test
    void toTransactionResponse_shouldMapCorrectly() {
        // Arrange
        Wallet source = new Wallet();
        Wallet destination = new Wallet();
        source.setUuid(UUID.randomUUID());
        destination.setUuid(UUID.randomUUID());

        Transaction transaction = Transaction.builder()
                .id(1L)
                .source(source)
                .destination(destination)
                .amount(new BigDecimal("100.50"))
                .status(TransactionStatus.MINED)
                .build();

        // Act
        TransactionResponse response = TransactionMapper.toTransactionResponse(transaction);

        // Assert
        assertEquals(1L, response.id());
        assertEquals(source.getUuid(), response.source());
        assertEquals(destination.getUuid(), response.destination());
        assertEquals(new BigDecimal("100.50"), response.amount());
        assertEquals(TransactionStatus.MINED, response.status());
    }

    @Test
    void toTransactionResponseList_shouldMapListCorrectly() {
        // Arrange
        Wallet source1 = new Wallet();
        Wallet dest1 = new Wallet();
        source1.setUuid(UUID.randomUUID());
        dest1.setUuid(UUID.randomUUID());

        Wallet source2 = new Wallet();
        Wallet dest2 = new Wallet();
        source2.setUuid(UUID.randomUUID());
        dest2.setUuid(UUID.randomUUID());

        Transaction tx1 = Transaction.builder()
                .id(1L)
                .source(source1)
                .destination(dest1)
                .amount(new BigDecimal("10.00"))
                .status(TransactionStatus.PENDING)
                .build();

        Transaction tx2 = Transaction.builder()
                .id(2L)
                .source(source2)
                .destination(dest2)
                .amount(new BigDecimal("50.00"))
                .status(TransactionStatus.INVALID)
                .build();

        List<Transaction> transactions = List.of(tx1, tx2);

        // Act
        List<TransactionResponse> responses = TransactionMapper.toTransactionResponseList(transactions);

        // Assert
        assertEquals(2, responses.size());

        TransactionResponse res1 = responses.get(0);
        assertEquals(1L, res1.id());
        assertEquals(source1.getUuid(), res1.source());
        assertEquals(dest1.getUuid(), res1.destination());
        assertEquals(new BigDecimal("10.00"), res1.amount());
        assertEquals(TransactionStatus.PENDING, res1.status());

        TransactionResponse res2 = responses.get(1);
        assertEquals(2L, res2.id());
        assertEquals(source2.getUuid(), res2.source());
        assertEquals(dest2.getUuid(), res2.destination());
        assertEquals(new BigDecimal("50.00"), res2.amount());
        assertEquals(TransactionStatus.INVALID, res2.status());
    }
}
