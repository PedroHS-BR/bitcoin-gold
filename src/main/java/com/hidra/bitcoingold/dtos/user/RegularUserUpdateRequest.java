package com.hidra.bitcoingold.dtos.user;

import lombok.Data;

@Data
public class RegularUserUpdateRequest {
    private String name;
    private String email;
    private String password;
}
