package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.dtos.wallet.BlockMinedResponse;
import com.hidra.bitcoingold.dtos.wallet.MineBlockRequest;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final TransactionService transactionService;
    private final BlockRepository blockRepository;

    @PostMapping
    public ResponseEntity<BlockMinedResponse> mineBlock(@RequestBody MineBlockRequest request) {
        if (!request.blockHash().startsWith("0000")) {
            throw new BadRequestException("Invalid block hash");
        }
        //BlockService.mineBlock(request.blockHash());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public void testFunctionality() {
        Block block = Block.builder()
                .blockHash("0000490a0bf3c4dddbc8ee424996c9990a80978fc2b1e6b48632ab3395685609")
                .timestamp(Instant.ofEpochMilli(1750287287630L))
                .nonce(51680)
                .build();
        blockRepository.save(block);
    }

}
