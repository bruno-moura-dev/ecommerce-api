package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserUpdateRoleDTO {

    @NotNull(message = "Role is required")
    private UserRole role;
}
