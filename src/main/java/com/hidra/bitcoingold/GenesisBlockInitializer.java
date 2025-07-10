package com.hidra.bitcoingold;

import com.hidra.bitcoingold.service.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenesisBlockInitializer implements ApplicationRunner {
    private final BlockService blockService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (blockService.isBlockchainEmpty()) {
            log.info("Blockchain is empty, creating genesis block");
            blockService.createGenesisBlock();
        }
        else log.info("Genesis block already exists");
    }
}
