package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.service.BlockService;
import com.hidra.bitcoingold.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final TransactionService transactionService;
    private final BlockRepository blockRepository;
    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<String> MineBlock() {
        blockService.mineBlock();
        return ResponseEntity.ok("Block mined");
    }


}
