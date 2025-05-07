package com.hidra.bitcoingold.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private String name;
    private String email;
}
