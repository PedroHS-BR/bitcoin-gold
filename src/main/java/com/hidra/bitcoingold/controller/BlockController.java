package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.service.BlockService;
import com.hidra.bitcoingold.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final TransactionService transactionService;
    private final BlockRepository blockRepository;
    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<Block> MineBlock() {
        Block block = blockService.createBlock();
        return new ResponseEntity<>(block, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Block> getBlock(@PathVariable long id) {

    }

}
