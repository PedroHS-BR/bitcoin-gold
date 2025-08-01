package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final BlockRepository blockRepository;
    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<Block> MineBlock() {
        Block block = blockService.createBlock();
        return new ResponseEntity<>(block, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Block> getBlock(@PathVariable long id) {
        return new ResponseEntity<>(blockService.getBlock(id), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Block>> getAllBlocks() {
        return new ResponseEntity<>(blockRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/validate/{id}")
    public ResponseEntity<String> validateBlock(@PathVariable long id) {
        return new ResponseEntity<>(blockService.ValidateBlock(id), HttpStatus.OK);
    }

}
