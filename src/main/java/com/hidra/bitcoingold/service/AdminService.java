package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) throw new BadRequestException("Role is required");
        String walletId = walletService.createWallet();
        user.setWalletId(walletId);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        User savedUser = findById(user.getId());
        UserService.updateFields(user, savedUser, passwordEncoder);
        if (user.getRole() != null) savedUser.setRole(user.getRole());
        return userRepository.save(savedUser);
    }

    public void deleteUser(UUID id) {
        User byId = findById(id);
        userRepository.delete(byId);
    }



}
