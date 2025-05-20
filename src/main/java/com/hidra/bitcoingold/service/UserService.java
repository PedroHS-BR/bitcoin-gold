package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.UserPostRequest;
import com.hidra.bitcoingold.dtos.UserResponse;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public void createUser(User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(User user) {
        User savedUser = findById(user.getId());
        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getPassword() != null) savedUser.setPassword(user.getPassword());
        if (user.getRole() != null) savedUser.setRole(user.getRole());
        userRepository.save(savedUser);
    }

    public void deleteUser(UUID id) {
        userRepository.delete(findById(id));
    }
}
