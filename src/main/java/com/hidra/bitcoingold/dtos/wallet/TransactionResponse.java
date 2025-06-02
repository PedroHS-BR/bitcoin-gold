package com.hidra.bitcoingold.dtos.wallet;

import java.math.BigDecimal;
import java.util.UUID;

public record TransactionResponse(
        Long id,
        UUID source,
        UUID destination,
        BigDecimal amount
) {
}
