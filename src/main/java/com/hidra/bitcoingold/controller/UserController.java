package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.UserResponse;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/regularUser/get")
    public ResponseEntity<UserResponse> getRegularUser() {
        User regularUser = userService.getRegularUser();
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(regularUser), HttpStatus.OK);
    }

    //    @PutMapping
//    public ResponseEntity<UserResponse> updateRegularUser(){
//
//    }

}
