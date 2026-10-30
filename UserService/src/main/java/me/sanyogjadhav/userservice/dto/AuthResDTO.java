package me.sanyogjadhav.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResDTO {
    private String token;
    private String tokenType;   // "Bearer"
    private long expiresIn;     // milliseconds
    private UserResDTO user;
}