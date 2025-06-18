package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.dtos.wallet.BlockMinedResponse;
import com.hidra.bitcoingold.dtos.wallet.MineBlockRequest;
import com.hidra.bitcoingold.dtos.wallet.TransactionRequest;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.TransactionMapper;
import com.hidra.bitcoingold.service.BlockService;
import com.hidra.bitcoingold.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<BlockMinedResponse> mineBlock(@RequestBody MineBlockRequest request) {
        if (!request.blockHash().startsWith("0000")) {
            throw new BadRequestException("Invalid block hash");
        }
        //BlockService.mineBlock(request.blockHash());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/test")
    public String testFunctionality(@RequestBody List<TransactionRequest> transactionRequests) {
        boolean b = transactionService.validateTransactionsData(transactionRequests);
        if (b) return "deu bom";
        else return "deu ruim";
    }

}
