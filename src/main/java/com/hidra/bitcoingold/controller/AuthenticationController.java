package com.hidra.bitcoingold.controller;


import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.user.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.user.TokenResponse;
import com.hidra.bitcoingold.dtos.user.UserLoginRequest;
import com.hidra.bitcoingold.dtos.user.UserResponse;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.security.TokenService;
import com.hidra.bitcoingold.service.AuthorizationService;
import com.hidra.bitcoingold.service.TransactionService;
import com.hidra.bitcoingold.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;
    private final UserService userService;
    private final TransactionService transactionService;


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

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerNewUser(@RequestBody @Valid RegisterUserPostRequest registerUserPostRequest) {
        User user = authorizationService.createRegularuser(UserMapper.INSTANCE.toUser(registerUserPostRequest));
        if (userService.howManyUsers() <= 100){
            transactionService.newUserBonusTransaction(user);
        }
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }

    @GetMapping("/connect")
    public ResponseEntity<String> connect() {
        return ResponseEntity.ok("Connecting to Bitcoin");
    }
}
