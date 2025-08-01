package com.hidra.bitcoingold.dtos.user;

import com.hidra.bitcoingold.domain.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserUpdateRequest {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
}
