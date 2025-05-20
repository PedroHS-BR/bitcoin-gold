package com.hidra.bitcoingold.dtos;

import com.hidra.bitcoingold.domain.UserRole;
import lombok.Data;

import java.util.UUID;

@Data
public class UserUpdateRequest {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
}
