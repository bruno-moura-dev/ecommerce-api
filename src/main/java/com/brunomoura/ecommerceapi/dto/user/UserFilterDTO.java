package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO {

    private Long id;

    private String name;

    private String email;

    private String cpf;

    private UserRole role;

    private Instant initialDateOfDelete;

    private Instant finalDateOfDelete;

    private Instant initialDateOfCreation;

    private Instant finalDateOfCreation;
}
