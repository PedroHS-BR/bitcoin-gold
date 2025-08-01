package com.hidra.bitcoingold.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.UserRole;
import com.hidra.bitcoingold.domain.Wallet;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.dtos.user.UserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserUpdateRequest;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import com.hidra.bitcoingold.service.AdminService;
import com.hidra.bitcoingold.service.WalletService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
public class UserAdminControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private WalletService walletService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private TransactionRepository transactionRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com", "123456789");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        User user = userRepository.findByEmail("pedro@gmail.com").orElseThrow();
        user.setRole(UserRole.ADMIN);
        userRepository.saveAndFlush(user);

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
    void findAll_ShouldReturnAllUsers() throws Exception {
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com2", "123456789");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("pedro@gmail.com", "pedro@gmail.com2")));
    }

    @Test
    void findUserById_ShouldReturnUser() throws Exception {
        User user = userRepository.findByEmail("pedro@gmail.com").orElseThrow();
        mockMvc.perform(get("/admin/" + user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("pedro@gmail.com"));
    }

    @Test
    void CreateUser_ShouldCreateUser() throws Exception {
        UserPostRequest userPostRequest = new UserPostRequest(
                "JOJO", "JOJO@gmail.com", "JOJOBIZARREADVENTURE", UserRole.ADMIN);

        mockMvc.perform(post("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(userPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("JOJO"))
                .andExpect(jsonPath("$.email").value("JOJO@gmail.com"))
                .andExpect(jsonPath("$.role").value("ADMIN"));

        User user = userRepository.findByEmail("JOJO@gmail.com")
                .orElseThrow(() -> new BadRequestException("User not found"));
        Assertions.assertEquals("JOJO", user.getName());
        Assertions.assertTrue(passwordEncoder.matches("JOJOBIZARREADVENTURE", user.getPassword()));
        Assertions.assertEquals(UserRole.ADMIN, user.getRole());

        Wallet wallet = walletService.getWallet(user.getWalletId());
        Assertions.assertNotNull(wallet);
    }

    @Test
    void UpdateUser_ShouldUpdateUser() throws Exception {
        User user = userRepository.findByEmail("pedro@gmail.com").orElseThrow();
        UserUpdateRequest build = UserUpdateRequest.builder().role(UserRole.ADMIN).id(user.getId()).build();
        mockMvc.perform(put("/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(build))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"));

        System.out.println(adminService.findById(build.getId()));
    }
    @Test
    void DeleteUser_ShouldDeleteUser() throws Exception {
        User user = userRepository.findByEmail("pedro@gmail.com").orElseThrow();
        mockMvc.perform(delete("/admin/" + user.getId())
        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        Assertions.assertThrows(Exception.class, () -> userRepository.findByEmail("pedro@gmail.com").orElseThrow());
    }

}
