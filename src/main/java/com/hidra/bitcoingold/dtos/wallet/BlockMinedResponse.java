package com.hidra.bitcoingold.dtos.wallet;

import java.time.Instant;

public record BlockMinedResponse(
        Long id,
        String blockHash,
        Instant timeStamp
) {
}
