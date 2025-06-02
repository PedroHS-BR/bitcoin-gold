package com.hidra.bitcoingold.dtos.wallet;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank String destination,
        @NotBlank BigDecimal amount
) {
}
