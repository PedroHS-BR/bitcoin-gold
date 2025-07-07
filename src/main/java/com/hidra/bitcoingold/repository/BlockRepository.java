package com.hidra.bitcoingold.repository;

import com.hidra.bitcoingold.domain.Block;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findTopByOrderByIdDesc();
}
