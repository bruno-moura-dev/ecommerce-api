package com.brunomoura.ecommerceapi.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ReactivateUserDTO {

    @NotNull(message = "Email is required")
    private String email;

    @NotNull(message = "Password is required")
    private String password;
}
