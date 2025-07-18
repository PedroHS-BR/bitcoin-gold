package com.hidra.bitcoingold.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@Builder
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