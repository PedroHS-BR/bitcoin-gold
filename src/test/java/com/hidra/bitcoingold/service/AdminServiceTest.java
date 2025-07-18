package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.util.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class AdminServiceTest {
    @InjectMocks
    private AdminService adminService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WalletService walletService;
    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        user = UserCreator.createValidUser();
        admin = UserCreator.createAdminUser();
    }

    @Test
    void findById_returnsUser_WhenUserExists() {
        BDDMockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        User byId = adminService.findById(user.getId());
        assertThat(byId.getEmail()).isEqualTo(user.getEmail());
        assertThat(byId).isEqualTo(user);
    }

    @Test
    void createUser_createsValidUser_WhenAllFieldsAreValid() {
        BDDMockito.when(passwordEncoder.encode(BDDMockito.anyString()))
                .thenReturn("Encrypted password");
        BDDMockito.when(userRepository.save(BDDMockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        BDDMockito.when(walletService.createWallet())
                .thenReturn("Encrypted wallet");

        User createdUser = adminService.createUser(user);

        assertThat(createdUser.getPassword()).isEqualTo("Encrypted password");
        assertThat(createdUser.getWalletId()).isEqualTo("Encrypted wallet");
        BDDMockito.verify(userRepository).save(BDDMockito.any(User.class));
    }

    @Test
    void updateUser_updatesValidUser_WhenAllFieldsAreValid() {
        BDDMockito.when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        BDDMockito.when(passwordEncoder.encode(BDDMockito.anyString()))
                .thenReturn("senhaCodificada");

        BDDMockito.when(userRepository.save(BDDMockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user1 = adminService.updateUser(user);

        assertThat(user1.getEmail()).isEqualTo(user.getEmail());
        assertThat(user1.getPassword()).isEqualTo("senhaCodificada");
        assertThat(user1.getName()).isEqualTo(user.getName());
        assertThat(user1.getRole()).isEqualTo(user.getRole());
        BDDMockito.verify(userRepository).save(BDDMockito.any(User.class));
    }

}