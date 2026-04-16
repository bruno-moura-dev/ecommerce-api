package com.brunomoura.ecommerceapi.config;

import com.brunomoura.ecommerceapi.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "admin")
@Validated
@Getter
public class AdminProperties {

    @NotBlank(message = "Username is required")
    private final String username;

    @NotBlank(message = "Password is required")
    private final String password;

    @NotNull(message = "Role is required")
    private final UserRole role;

    public AdminProperties(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = UserRole.ADMIN;
    }
}
