package com.hidra.bitcoingold.dtos.user;

import com.hidra.bitcoingold.domain.UserRole;
import jakarta.validation.constraints.NotBlank;

public record UserPostRequest(
        @NotBlank (message = "The name must not be null, empty, or contain only whitespace.")
        String name,
        @NotBlank (message = "The email must not be null, empty, or contain only whitespace.")
        String email,
        @NotBlank (message = "The password must not be null, empty, or contain only whitespace.")
        String password,
        UserRole role
) {}
