package com.hidra.bitcoingold.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegularUserUpdateRequest {
    private String name;
    private String email;
    private String password;
}
