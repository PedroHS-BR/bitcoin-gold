package com.hidra.bitcoingold.repository;

import com.hidra.bitcoingold.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
