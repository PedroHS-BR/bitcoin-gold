package com.hidra.bitcoingold.repository;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.id IN :ids
        AND t.source.uuid IS NOT NULL
        AND t.destination.uuid IS NOT NULL
    """)
    List<Transaction> findPendingWithValidWalletsByIds(@Param("ids") List<Long> ids);

    List<Transaction> findTop100ByStatus(TransactionStatus status);
}

//AND t.status = com.hidra.bitcoingold.domain.TransactionStatus.PENDING
