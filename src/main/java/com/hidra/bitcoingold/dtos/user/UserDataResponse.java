package com.hidra.bitcoingold.dtos.user;

import com.hidra.bitcoingold.domain.UserRole;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UserDataResponse {
    private UUID id;
    private String name;
    private String email;
    private UserRole role;
    private BigDecimal balance;
}
