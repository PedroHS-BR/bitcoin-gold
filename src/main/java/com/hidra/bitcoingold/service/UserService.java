package com.hidra.bitcoingold.service;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.UserLoginRequest;
import com.hidra.bitcoingold.dtos.UserResponse;
import com.hidra.bitcoingold.dtos.UserPostRequest;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> findAll() {
        List<User> allUsers = userRepository.findAll();
        return UserMapper.INSTANCE.toUserResponseList(allUsers);

    }

    public User findById(UUID id) {
        return userRepository.findUserById(id).orElseThrow(() -> new BadRequestException("User not found"));
    }

    public void createUser(UserPostRequest userPostRequest) {
        User user = UserMapper.INSTANCE.toUser(userPostRequest);
        userRepository.save(user);
    }

    public UserResponse login(UserLoginRequest userLoginRequest) {
        User savedUser = findByEmail(userLoginRequest.getEmail());
        if (savedUser != null && savedUser.getPassword().equals(userLoginRequest.getPassword())) {
            return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
        }
        throw new BadRequestException("Login or password is incorrect");
    }

    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public void updateUser(User user) {
        User savedUser = findById(user.getId());
        if (user.getEmail() == null) user.setEmail(savedUser.getEmail());
        if (user.getName() == null) user.setName(savedUser.getName());
        if (user.getPassword() == null) user.setPassword(savedUser.getPassword());
        userRepository.save(user);
    }

    @Transactional
    public void deleteAll() {
        userRepository.deleteAll();
    }

    public void deleteUser(UUID id) {
        userRepository.delete(findById(id));
    }
}
