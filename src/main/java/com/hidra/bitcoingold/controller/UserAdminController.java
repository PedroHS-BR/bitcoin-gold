package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.UserPostRequest;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.dtos.user.UserUpdateRequest;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final AdminService adminService;

    @GetMapping
    public List<UserResponse> findAll() {
        return UserMapper.INSTANCE.toUserResponseList(adminService.findAll());
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable UUID id) {
        return UserMapper.INSTANCE.toUserResponse(adminService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserPostRequest userPostRequest) {
        User user = adminService.createUser(UserMapper.INSTANCE.toUser(userPostRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        User user = adminService.updateUser(UserMapper.INSTANCE.toUser(userUpdateRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID id) {
        adminService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
