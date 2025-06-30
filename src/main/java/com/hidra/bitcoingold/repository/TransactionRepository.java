package com.hidra.bitcoingold.repository;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import com.hidra.bitcoingold.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {


    List<Transaction> findTop100ByStatus(TransactionStatus status);

    List<Transaction> findBySourceAndStatus(Wallet source, TransactionStatus status);

    List<Transaction> findBySource(Wallet source);
}

