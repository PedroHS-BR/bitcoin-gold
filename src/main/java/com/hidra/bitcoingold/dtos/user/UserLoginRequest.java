package com.hidra.bitcoingold.dtos.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {}
