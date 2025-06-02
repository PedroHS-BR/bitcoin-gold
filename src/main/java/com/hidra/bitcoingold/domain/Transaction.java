package com.hidra.bitcoingold.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "source_uuid")
    private Wallet source;


    @ManyToOne
    @JoinColumn(name = "destination_uuid")
    private Wallet destination;

    @Column(nullable = false)
    private BigDecimal amount;

}
