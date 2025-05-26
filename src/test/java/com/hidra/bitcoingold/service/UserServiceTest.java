package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.UserRepository;
import com.hidra.bitcoingold.util.UserCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepositoryMock;
    private User user;

    @BeforeEach
    void setUp() {
        user = UserCreator.createValidUser();
        List<User> users = List.of(user, user);
        BDDMockito.when(userRepositoryMock.findAll())
                .thenReturn(users);
        BDDMockito.when(userRepositoryMock.findById(user.getId()))
                .thenReturn(Optional.of(user));

    }

    @Test
    void findAll_returnsAllUsers_WhenUserExists() {
        List<User> all = userService.findAll();
        assertThat(all).hasSize(2).containsExactlyInAnyOrder(user, user);
    }

    @Test
    void findAll_returnsEmptyList_WhenUserNotExists() {
        List<User> all = List.of();
        assertThat(all).isEmpty();
    }

    @Test
    void findById_returnsUser_WhenUserExists() {
        User byId = userService.findById(user.getId());
        assertThat(byId).isEqualTo(user);
    }
    @Test
    void findById_ThrowsException_WhenUserNotExists() {
        UUID nonexistentId = UUID.randomUUID();
        assertThatThrownBy(() -> userService.findById(nonexistentId))
        .isInstanceOf(BadRequestException.class);
    }


    @Test
    void createUser() {

    }

    @Test
    void updateUser() {
    }

    @Test
    void deleteUser() {
    }
}