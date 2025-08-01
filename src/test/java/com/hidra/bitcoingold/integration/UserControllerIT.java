package com.hidra.bitcoingold.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.UserRole;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.RegularUserUpdateRequest;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.repository.TransactionRepository;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.repository.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.profiles.active=test")
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    private String token;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        RegisterUserPostRequest request = new RegisterUserPostRequest(
                "Pedro", "pedro@gmail.com", "123456789");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

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
    void getRegularUser_shouldReturnUser() throws Exception {
        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.balance").value("0.0"));
    }

    @Test
    void updateRegularUser_shouldUpdateUserSuccessfully() throws Exception {
        RegularUserUpdateRequest user = new RegularUserUpdateRequest(
                "JOJO", "JOJO@gmail.com", "1234");
        User savedUser = userRepository.findByEmail("pedro@gmail.com").orElseThrow();

        mockMvc.perform(put("/user")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()))
                .andExpect(jsonPath("$.name").value("JOJO"))
                .andExpect(jsonPath("$.email").value("JOJO@gmail.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        User updatedUser = userRepository.findByEmail("JOJO@gmail.com").orElseThrow();

        Assertions.assertEquals("JOJO", updatedUser.getName());
        Assertions.assertEquals("JOJO@gmail.com", updatedUser.getEmail());
        Assertions.assertTrue(passwordEncoder.matches("1234", updatedUser.getPassword()));
        Assertions.assertEquals(UserRole.USER, updatedUser.getRole());
    }

    @Test
    void deleteRegularUser_shouldDeleteUserSuccessfully() throws Exception {
        User savedUser = userRepository.findByEmail("pedro@gmail.com").orElseThrow();
        mockMvc.perform(delete("/user")
        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedUser.getId().toString()));

        Assertions.assertThrows(Exception.class, () -> userRepository.findByEmail("pedro@gmail.com").orElseThrow());
    }

}
