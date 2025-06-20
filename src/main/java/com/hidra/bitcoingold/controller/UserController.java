package com.hidra.bitcoingold.controller;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.RegularUserUpdateRequest;
import com.hidra.bitcoingold.dtos.user.UserDataResponse;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.service.UserService;
import com.hidra.bitcoingold.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final WalletService walletService;

    @GetMapping()
    public ResponseEntity<UserDataResponse> getRegularUser() {
        User regularUser = userService.getRegularUser();
        UserDataResponse user = UserMapper.INSTANCE.toUserDataResponse(regularUser);
        user.setBalance(walletService.getWallet(regularUser.getWalletId()).getBalance());
        return new ResponseEntity<>(user , HttpStatus.OK);
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
