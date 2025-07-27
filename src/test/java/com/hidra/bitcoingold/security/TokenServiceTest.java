package com.hidra.bitcoingold.security;

import com.hidra.bitcoingold.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() throws Exception {
        tokenService = new TokenService();
        // Injetar valor do secret via reflexão (simula @Value)
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        String secret = "mysecret1234567890";
        secretField.set(tokenService, secret);
    }

    @Test
    void generateToken_ShouldReturnTokenString() {
        User user = new User();
        user.setEmail("user@test.com");

        String token = tokenService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT tem 3 partes separadas por ponto
    }

    @Test
    void verifyToken_ShouldReturnSubject_WhenTokenIsValid() {
        User user = new User();
        user.setEmail("user@test.com");

        String token = tokenService.generateToken(user);
        String subject = tokenService.verifyToken(token);

        assertEquals(user.getEmail(), subject);
    }

    @Test
    void verifyToken_ShouldReturnEmptyString_WhenTokenIsInvalid() {
        String invalidToken = "invalid.token.string";
        String subject = tokenService.verifyToken(invalidToken);
        assertEquals("", subject);
    }

    @Test
    void generateToken_ShouldThrowRuntimeException_WhenSecretIsInvalid() throws Exception {
        TokenService badService = new TokenService();
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(badService, ""); // segredo vazio

        User user = new User();
        user.setEmail("user@test.com");

        assertThrows(RuntimeException.class, () -> badService.generateToken(user));
    }

}
