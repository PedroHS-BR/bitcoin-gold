package com.hidra.bitcoingold.util;

import com.hidra.bitcoingold.domain.User;
import com.hidra.bitcoingold.domain.UserRole;

import java.util.UUID;

public class UserCreator {

    public static User createValidUser(){
        UUID uuid = UUID.fromString("5964a8a5-9b25-4565-b96c-33df7067753c");
        return User.builder()
                .id(uuid)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .role(UserRole.USER)
                .build();
    }

    public static User createAdminUser(){
        UUID uuid = UUID.fromString("5964a8a5-9b25-4565-b96c-33df7067753c");
        return User.builder()
                .id(uuid)
                .name("Hidra")
                .email("hidra@example.com")
                .password("HIDRA secret password")
                .role(UserRole.ADMIN)
                .build();
    }
}
