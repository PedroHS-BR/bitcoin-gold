package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public User getRegularUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return userRepository.findByEmail(((UserDetails) principal).getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }
        return null;
    }

    public User updateUser(User user) {
        User savedUser = getRegularUser();
        updateFields(user, savedUser, passwordEncoder);
        userRepository.save(savedUser);
        return savedUser;
    }

    static void updateFields(User user, User savedUser, PasswordEncoder passwordEncoder) {
        if (user.getEmail() != null) savedUser.setEmail(user.getEmail());
        if (user.getName() != null) savedUser.setName(user.getName());
        if (user.getPassword() != null) {
            String encode = passwordEncoder.encode(user.getPassword());
            savedUser.setPassword(encode);
        }
    }

    public User deleteUser() {
        User regularUser = getRegularUser();
        walletService.deleteWallet(regularUser.getWalletId());
        userRepository.delete(regularUser);
        return regularUser;
    }
}
