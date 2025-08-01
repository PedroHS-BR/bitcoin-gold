package com.hidra.bitcoingold.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidra.bitcoingold.domain.Transaction;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.UserRole;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.WalletService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
public class AuthenticationControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void testConnect() throws Exception {
        mockMvc.perform(get("/auth/connect"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connecting to Bitcoin"));
    }

    @Test
    void register_ShouldCreateNewUser() throws Exception {
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com", "123456789");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        User user = userRepository.findByEmail("pedro@gmail.com")
                .orElseThrow(() -> new BadRequestException("User not found"));
        Assertions.assertEquals("Pedro", user.getName());
        Assertions.assertEquals("pedro@gmail.com", user.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("123456789", user.getPassword()));
        Assertions.assertEquals(UserRole.USER, user.getRole());

        Wallet wallet = walletService.getWallet(user.getWalletId());
        Assertions.assertNotNull(wallet);

        List<Transaction> pendingTransactions = transactionService.getPendingTransactions();
        assertThat(pendingTransactions).isNotEmpty();
    }

    @Test
    void login_ShouldBeSuccessful() throws Exception {
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com", "123456789");

        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "pedro@gmail.com", "123456789"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_ShouldNotBeSuccessful() throws Exception {
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com", "123456789");

        UserLoginRequest userLoginRequest = new UserLoginRequest(
                "pedro@gmail.com", "123"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details").value("Invalid email or password"));
    }

}
