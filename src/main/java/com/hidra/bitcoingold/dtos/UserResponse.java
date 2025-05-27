package com.hidra.bitcoingold.dtos;

import com.hidra.bitcoingold.domain.UserRole;

public record UserResponse(
        String name,
        String email,
        UserRole role
) {}
