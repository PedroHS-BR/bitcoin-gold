package com.hidra.bitcoingold.dtos.wallet;

import com.hidra.bitcoingold.domain.Transaction;

import java.time.Instant;
import java.util.List;

public record MineBlockRequest(
        String blockHash,
        List<Transaction> transactionList,
        Long nonce,
        Instant timeStamp
) {
}
