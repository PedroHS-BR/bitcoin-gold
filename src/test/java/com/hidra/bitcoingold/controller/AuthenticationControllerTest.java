package com.hidra.bitcoingold.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.TokenResponse;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.security.TokenService;
import com.hidra.bitcoingold.service.AuthorizationService;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() throws Exception {
        User user = User.builder().id(UUID.randomUUID()).email("user@test.com").password("123456").build();
        UserLoginRequest loginRequest = new UserLoginRequest("user@test.com", "123456");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenService.generateToken(user)).thenReturn("mocked.jwt.token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"));
    }

    @Test
    void register_ShouldCreateUserAndReturn201() throws Exception {
        RegisterUserPostRequest request = new RegisterUserPostRequest("John Doe", "john@test.com", "123456");
        User user = UserMapper.INSTANCE.toUser(request);
        user.setId(UUID.randomUUID());

        when(authorizationService.createRegularuser(any(User.class))).thenReturn(user);
        when(userService.howManyUsers()).thenReturn(50L); // para ativar bonus

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("john@test.com"));

        verify(transactionService).newUserBonusTransaction(user);
    }

    @Test
    void connect_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/auth/connect"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connecting to Bitcoin"));
    }
}
