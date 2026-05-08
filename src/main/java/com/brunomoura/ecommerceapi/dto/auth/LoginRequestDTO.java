package com.brunomoura.ecommerceapi.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginRequestDTO {

    @Schema(example = "admin@test.com", description = "User email")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(example = "Admin@123", description = "User password")
    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequestDTO() {
    }
}