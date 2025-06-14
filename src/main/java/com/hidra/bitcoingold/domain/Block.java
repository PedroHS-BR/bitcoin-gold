package com.hidra.bitcoingold.domain;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String previousHash;

    private String transactionHash;

    private String blockHash;

    private long nonce;

    private Instant timestamp;

    @ManyToOne
    @JoinColumn(name = "miner_wallet_uuid")
    private Wallet Miner;
}
