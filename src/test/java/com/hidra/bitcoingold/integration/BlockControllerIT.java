package com.hidra.bitcoingold.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidra.bitcoingold.domain.Block;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.repository.BlockRepository;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.service.BlockService;
import com.hidra.bitcoingold.service.WalletService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BlockControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private String token;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private WalletService walletService;
    @Autowired
    private BlockService blockService;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com", "123456789");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        RegisterUserPostRequest request2 = new RegisterUserPostRequest(
                "JOJO", "JOJO@gmail.com", "123456789");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        UserLoginRequest loginRequest = new UserLoginRequest(
                "pedro@gmail.com", "123456789");

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(loginResponse);
        token = jsonNode.get("token").asText();

        Optional<Block> blockById = blockRepository.getBlockById(2L);
        blockById.ifPresent(block -> blockRepository.delete(block));
    }

    @Test
    void getBlock_ShouldGetGenesisBlock() throws Exception {
        long blockId = 1;
        Block block = blockRepository.getBlockById(blockId).orElseThrow();
        mockMvc.perform(get("/block/" + blockId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(blockId))
                .andExpect(jsonPath("$.nonce").value(block.getNonce()))
                .andExpect(jsonPath("$.timestamp").value(block.getTimestamp()))
                .andExpect(jsonPath("$.blockHash").value(block.getBlockHash()))
                .andExpect(jsonPath("$.previousHash").value(block.getPreviousHash()))
                .andExpect(jsonPath("$.transactionHash").value(block.getTransactionHash()));
    }

    @Test
    void mineBlock_ShouldCreateABlock_AndValidateTransactions() throws Exception {

        mockMvc.perform(post("/block")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
        List<Block> all = blockRepository.findAll();
        Block block = all.get(1);

        mockMvc.perform(get("/block/" + block.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(block.getId()))
                .andExpect(jsonPath("$.nonce").value(block.getNonce()))
                .andExpect(jsonPath("$.timestamp").value(block.getTimestamp()))
                .andExpect(jsonPath("$.blockHash").value(block.getBlockHash()))
                .andExpect(jsonPath("$.previousHash").value(block.getPreviousHash()))
                .andExpect(jsonPath("$.transactionHash").value(block.getTransactionHash()));

        mockMvc.perform(get("/transaction")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status", everyItem(is("MINED"))));

        Wallet wallet1 = walletService.getWalletByEmail("pedro@gmail.com");
        Wallet wallet2 = walletService.getWalletByEmail("JOJO@gmail.com");

        assertThat(wallet1.getBalance()).isEqualTo(wallet2.getBalance()).isEqualTo("50.0000");
    }

    @Test
    void getAllBlocks_ShouldReturnAllBlocks() throws Exception {
        mockMvc.perform(post("/block")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/block/all")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].nonce").isNotEmpty())
                .andExpect(jsonPath("$[*].timestamp").isNotEmpty())
                .andExpect(jsonPath("$[*].blockHash").isNotEmpty());
    }

    @Test
    @Order(1)
    void validateBlock_ShouldValidateBlocks() throws Exception {
        mockMvc.perform(post("/block")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        List<Block> all = blockRepository.findAll();
        Block block1 = all.getFirst();
        Block block2 = all.get(1);
        System.out.println(block2);
        mockMvc.perform(get("/block/validate/" + block1.getId())
        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Valid block, Block hash: " + block1.getBlockHash()));


        String s = blockService.sha256(block2.getPreviousHash() + block2.getTransactionHash() + block2.getNonce() + block2.getTimestamp()+ block2.getMiner());
        System.out.println(s);

        mockMvc.perform(get("/block/validate/" + block2.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Valid block, Block hash: " + block2.getBlockHash()));
    }
}
