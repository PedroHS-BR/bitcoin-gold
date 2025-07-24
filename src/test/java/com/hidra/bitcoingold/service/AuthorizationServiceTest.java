package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.UserRole;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @InjectMocks
    private AuthorizationService authorizationService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private WalletService walletService;

    @Test
    void createRegularuser_shouldCreateUserWithEncodedPasswordAndWallet() {
        // Arrange
        User user = new User();
        user.setPassword("mySecret");
        user.setEmail("email@test.com");

        String encodedPassword = "encoded";
        String walletId = UUID.randomUUID().toString();

        given(passwordEncoder.encode("mySecret")).willReturn(encodedPassword);
        given(walletService.createWallet()).willReturn(walletId);
        given(userRepository.save(ArgumentMatchers.any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = authorizationService.createRegularuser(user);

        // Assert
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(UserRole.USER, result.getRole());
        assertEquals(walletId, result.getWalletId());

        BDDMockito.verify(passwordEncoder).encode("mySecret");
        BDDMockito.verify(walletService).createWallet();
        BDDMockito.verify(userRepository).save(result);
    }

    @Test
    void createRegularuser_shouldThrowException_whenPasswordIsTooLong() {
        // Arrange
        String longPassword = "a".repeat(73); // 73 caracteres ASCII = 73 bytes
        User user = new User();
        user.setPassword(longPassword);

        // Act + Assert
        assertThrows(BadRequestException.class, () -> authorizationService.createRegularuser(user));

        BDDMockito.verify(passwordEncoder, never()).encode(ArgumentMatchers.any());
        BDDMockito.verify(userRepository, never()).save(ArgumentMatchers.any());
    }

    @Test
    void loadUserByUsername_shouldReturnUser_whenFound() {
        // Arrange
        String email = "email@test.com";
        User user = new User();
        user.setEmail(email);

        given(userRepository.findUserByEmail(email)).willReturn(user);

        // Act
        User result = (User) authorizationService.loadUserByUsername(email);

        // Assert
        assertEquals(email, result.getEmail());
        BDDMockito.verify(userRepository).findUserByEmail(email);
    }

}