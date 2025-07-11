package com.hidra.bitcoingold.repository;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    List<Transaction> findTop100ByStatus(TransactionStatus status);

    List<Transaction> findBySourceAndStatus(Wallet source, TransactionStatus status);

    List<Transaction> findBySource(Wallet source);

    List<Transaction> findTop100ByStatusOrderByIdAsc(TransactionStatus status);

    List<Transaction> findByBlock(Block block);
}

