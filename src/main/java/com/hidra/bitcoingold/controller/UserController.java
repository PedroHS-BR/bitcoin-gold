package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.*;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.security.TokenService;
import com.hidra.bitcoingold.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @GetMapping
    public List<UserResponse> findAll() {
        return UserMapper.INSTANCE.toUserResponseList(userService.findAll());
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return UserMapper.INSTANCE.toUserResponse(userService.findById(id));
    }

    @GetMapping("/regularUser/get")
    public ResponseEntity<UserResponse> getRegularUser() {
        User regularUser = userService.getRegularUser();
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(regularUser), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserPostRequest userPostRequest) {
        User user = userService.createUser(UserMapper.INSTANCE.toUser(userPostRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }

    @PostMapping("/regularUser/new")
    public ResponseEntity<UserResponse> createRegularUser(@RequestBody @Valid CommonUserPostRequest commonUserPostRequest) {
        User user = userService.createRegularuser(UserMapper.INSTANCE.toUser(commonUserPostRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid UserLoginRequest userLoginRequest) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginRequest.email(), userLoginRequest.password());
            Authentication auth = this.authenticationManager.authenticate(usernamePassword);
            String token = tokenService.generateToken((User) auth.getPrincipal());
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (Exception e) {
            throw new BadRequestException("Invalid email or password");
        }
    }

    @PutMapping
    public void updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        userService.updateUser(UserMapper.INSTANCE.toUser(userUpdateRequest));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }

}
