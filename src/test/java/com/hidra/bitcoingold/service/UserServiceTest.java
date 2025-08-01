package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.util.UserCreator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDetails userDetails;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private UserRepository userRepository;


    private User user;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = UserCreator.createValidUser();
    }
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


    @Test
    void updateFields_doesNotUpdateFields_WhenInputFieldsAreNull() {
        User input = new User(); // todos os campos são null
        User saved = new User();
        saved.setEmail("original@email.com");
        saved.setName("Original Name");
        saved.setPassword("originalPassword");

        PasswordEncoder encoder = BDDMockito.mock(PasswordEncoder.class);

        UserService.updateFields(input, saved, encoder);

        assertThat(saved.getEmail()).isEqualTo("original@email.com");
        assertThat(saved.getName()).isEqualTo("Original Name");
        assertThat(saved.getPassword()).isEqualTo("originalPassword");

        verify(encoder, BDDMockito.never()).encode(BDDMockito.anyString());
    }

    @Test
    void getRegularUser_shouldReturnUser_whenUserExistsAndIsAuthenticated() {
        // Arrange
        String email = "test@example.com";
        user.setEmail(email);

        when(userDetails.getUsername()).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        SecurityContextHolder.setContext(securityContext);

        // Act
        User result = userService.getRegularUser();

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getRegularUser_shouldThrowException_whenUserNotFound() {
        // Arrange
        String email = "notfound@example.com";

        when(userDetails.getUsername()).thenReturn(email);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        SecurityContextHolder.setContext(securityContext);

        // Act + Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getRegularUser());
    }

}