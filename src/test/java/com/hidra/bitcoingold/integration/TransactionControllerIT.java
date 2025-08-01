package com.hidra.bitcoingold.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.dtos.wallet.CreateTransactionRequest;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import com.hidra.bitcoingold.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
public class TransactionControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    private String token;
    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;


    @BeforeEach
    void setUp() throws Exception {
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
    }

    @Test
    void createTransaction_ShouldCreateTransaction() throws Exception {
        Wallet sourceWallet = walletService.getWalletByEmail("pedro@gmail.com");
        sourceWallet.setBalance(BigDecimal.valueOf(100));
        walletRepository.save(sourceWallet);
        Wallet destinationWallet = walletService.getWalletByEmail("JOJO@gmail.com");

        CreateTransactionRequest request = new CreateTransactionRequest("JOJO@gmail.com", BigDecimal.TEN);
        mockMvc.perform(post("/transaction")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.source").value(sourceWallet.getUuid().toString()))
                .andExpect(jsonPath("$.destination").value(destinationWallet.getUuid().toString()))
                .andExpect(jsonPath("$.amount").value(10));

        List<Transaction> transactions = transactionRepository.findBySource(sourceWallet);
        Assertions.assertEquals(transactions.getFirst().getSource().getUuid(), sourceWallet.getUuid());
        Assertions.assertEquals(transactions.getFirst().getDestination().getUuid(), destinationWallet.getUuid());
        Assertions.assertEquals(0, BigDecimal.valueOf(10.00).compareTo(transactions.getFirst().getAmount()));
    }

    @Test
    void getTransactions_ShouldReturnAllTransactions() throws Exception {
        Wallet wallet1 = walletService.getWalletByEmail("pedro@gmail.com");
        Wallet wallet2 = walletService.getWalletByEmail("JOJO@gmail.com");

        mockMvc.perform(get("/transaction")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].destination", containsInAnyOrder(
                        wallet1.getUuid().toString(), wallet2.getUuid().toString())))
                .andExpect(jsonPath("$[*].source", everyItem(is("84741a3d-ff44-45fe-af84-fe9e05079ef8"))));
    }

    @Test
    void getUserTransactions_ShouldReturnAllUserTransactions() throws Exception {
        Wallet sourceWallet = walletService.getWalletByEmail("pedro@gmail.com");
        sourceWallet.setBalance(BigDecimal.valueOf(100));
        walletRepository.save(sourceWallet);
        Wallet destinationWallet = walletService.getWalletByEmail("JOJO@gmail.com");

        CreateTransactionRequest request = new CreateTransactionRequest("JOJO@gmail.com", BigDecimal.TEN);
        mockMvc.perform(post("/transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/transaction/user")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[*].destination", containsInAnyOrder(destinationWallet.getUuid().toString())))
                .andExpect(jsonPath("$[*].source", containsInAnyOrder(sourceWallet.getUuid().toString())))
                .andExpect(jsonPath("$[*].amount").value(10.0));
    }

    @Test
    void getPendingTransactions_ShouldReturnAllPendingTransactions() throws Exception {
        Wallet wallet1 = walletService.getWalletByEmail("pedro@gmail.com");
        Wallet wallet2 = walletService.getWalletByEmail("JOJO@gmail.com");

        mockMvc.perform(get("/transaction/pending")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].destination", containsInAnyOrder(
                        wallet1.getUuid().toString(), wallet2.getUuid().toString())))
                .andExpect(jsonPath("$[*].source", everyItem(is("84741a3d-ff44-45fe-af84-fe9e05079ef8"))))
                .andExpect(jsonPath("$[*].status", everyItem(is("PENDING"))));
    }
}
