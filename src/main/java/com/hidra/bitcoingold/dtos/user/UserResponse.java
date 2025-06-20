package com.hidra.bitcoingold.dtos.user;

import com.hidra.bitcoingold.domain.UserRole;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        UserRole role
) {}
