package com.hidra.bitcoingold.dtos;

import com.hidra.bitcoingold.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPostRequest {
    @NotBlank(message = "The name must not be null, empty, or contain only whitespace.")
    private String name;
    @NotBlank(message = "The email must not be null, empty, or contain only whitespace.")
    private String email;
    @NotBlank(message = "The password must not be null, empty, or contain only whitespace.")
    private String password;
    @NotBlank
    private UserRole role;
}
