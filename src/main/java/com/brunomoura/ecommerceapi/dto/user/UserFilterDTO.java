package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserFilterDTO {

    @Schema(example = "1", description = "User's id")
    private Long id;

    @Schema(example = "Fátima", description = "User's name")
    private String name;

    @Schema(example = "fatima_goncalves@email.com", description = "User's email")
    private String email;

    @Schema(example = "20354586920", description = "User's CPF")
    private String cpf;

    @Schema(example = "ADMIN", description = "User's role")
    private UserRole role;

    @Schema(example = "01/05/2026", description = "Initial deletion date")
    private Instant initialDateOfDelete;

    @Schema(example = "31/12/2026", description = "Final deletion date")
    private Instant finalDateOfDelete;

    @Schema(example = "01/05/2026", description = "Initial creation date")
    private Instant initialDateOfCreation;

    @Schema(example = "31/12/2026", description = "Final creation date")
    private Instant finalDateOfCreation;
}
