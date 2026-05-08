package com.brunomoura.ecommerceapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReactivateUserDTO {

    @Schema(example = "fatima@email.com", description = "User's email")
    @NotNull(message = "Email is required")
    private String email;

    @Schema(example = "NewPassword@123", description = "User's current password")
    @NotNull(message = "Password is required")
    private String password;
}
