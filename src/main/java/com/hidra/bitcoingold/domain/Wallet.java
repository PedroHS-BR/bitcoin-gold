package com.hidra.bitcoingold.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Wallet {
    @Id
    private UUID uuid;
    @Column(precision = 19, scale = 4)
    private BigDecimal balance;

    public Wallet() {
        this.uuid = UUID.randomUUID();
        balance = BigDecimal.ZERO;
    }
}