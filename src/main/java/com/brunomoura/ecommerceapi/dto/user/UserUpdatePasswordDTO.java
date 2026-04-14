package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.validation.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserUpdatePasswordDTO {

    @NotBlank(message = "Current password is required.")
    @ValidPassword
    private String currentPassword;

    @NotBlank(message = "New password is required.")
    @ValidPassword
    private String newPassword;
}