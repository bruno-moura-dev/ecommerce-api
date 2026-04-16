package com.brunomoura.ecommerceapi.dto.auth;

import lombok.Getter;

import java.time.Instant;

@Getter
public class LoginResponseDTO {

    private final String token;

    private final Instant expiresAt;

    private final String type;

    public LoginResponseDTO(String token, Instant expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
        this.type = "Bearer ";
    }

}