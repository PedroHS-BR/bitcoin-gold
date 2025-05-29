package com.hidra.bitcoingold.dtos;

import com.hidra.bitcoingold.domain.UserRole;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {}
