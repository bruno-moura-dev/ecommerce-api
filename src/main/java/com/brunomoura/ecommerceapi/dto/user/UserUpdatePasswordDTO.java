package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserUpdatePasswordDTO {

    @Schema(example = "Password@123", description = "User's current password")
    @NotBlank(message = "Current password is required.")
    @ValidPassword
    private String currentPassword;

    @Schema(example = "NewPassword@123", description = "User's new password")
    @NotBlank(message = "New password is required.")
    @ValidPassword
    private String newPassword;
}