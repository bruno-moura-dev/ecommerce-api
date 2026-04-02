package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter @Setter
public class UserFilterDTO {

    private Long id;

    private String name;

    private String cpf;

    private String email;

    private UserRole role;

    private Instant initialDateOfDelete;

    private Instant finalDateOfDelete;

    private Instant initialDateOfCreation;

    private Instant finalDateOfCreation;
}
