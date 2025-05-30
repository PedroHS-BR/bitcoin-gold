package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.RegularUserUpdateRequest;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserResponse> getRegularUser() {
        User regularUser = userService.getRegularUser();
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(regularUser), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateRegularUser(@RequestBody RegularUserUpdateRequest regularUserUpdateRequest) {
        User user = userService.updateUser(UserMapper.INSTANCE.toUser(regularUserUpdateRequest));
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<UserResponse> deleteRegularUser() {
        User user = userService.deleteUser();
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.OK);
    }

}
