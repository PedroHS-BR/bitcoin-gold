package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.UserLoginRequest;
import com.hidra.bitcoingold.dtos.UserResponse;
import com.hidra.bitcoingold.dtos.UserPostRequest;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return UserMapper.INSTANCE.toUserResponse(userService.findById(id));
    }

    @PostMapping
    public void createUser(@RequestBody UserPostRequest userPostRequest) {
        userService.createUser(userPostRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        return new ResponseEntity<>(userService.login(userLoginRequest), HttpStatus.OK);
    }

    @PutMapping
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

    @DeleteMapping("/all")
    public void deleteAll(){
        userService.deleteAll();
    }
}
