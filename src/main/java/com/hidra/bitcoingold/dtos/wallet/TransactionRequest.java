package com.hidra.bitcoingold.dtos.wallet;

import com.hidra.bitcoingold.domain.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionRequest(
        Long id,
        UUID source,
        UUID destination,
        BigDecimal amount,
        TransactionStatus status
) {
}
