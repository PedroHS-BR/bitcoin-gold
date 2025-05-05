package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void createUser(User user) {
        if (user.getId() != null) throw new BadRequestException("The user id should be null");
        userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    public void updateUser(User user) {
        findById(user.getId());
        userRepository.save(user);
    }

    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
        userRepository.resetAutoIncrement();
    }
}
