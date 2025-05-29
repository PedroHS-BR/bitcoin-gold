package com.hidra.bitcoingold.controller;


import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.dtos.RegisterUserPostRequest;
import com.hidra.bitcoingold.dtos.TokenResponse;
import com.hidra.bitcoingold.dtos.UserLoginRequest;
import com.hidra.bitcoingold.dtos.UserResponse;
import com.hidra.bitcoingold.exception.BadRequestException;
import com.hidra.bitcoingold.mapper.UserMapper;
import com.hidra.bitcoingold.security.TokenService;
import com.hidra.bitcoingold.service.AdminService;
import com.hidra.bitcoingold.service.AuthorizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final AdminService adminService;
    private final TokenService tokenService;
    private final AuthorizationService authorizationService;


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
        return new ResponseEntity<>(UserMapper.INSTANCE.toUserResponse(user), HttpStatus.CREATED);
    }
}
