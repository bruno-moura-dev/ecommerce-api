package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.validation.annotation.ValidDateOfBirth;
import com.brunomoura.ecommerceapi.validation.annotation.ValidPassword;
import com.brunomoura.ecommerceapi.validation.annotation.ValidPhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.Set;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateRequestDTO {

    @Schema(example = "Fátima Antonella Gonçalves", description = "User's name")
    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters long.")
    private String name;

    @Schema(example = "fatima_goncalves@email.com", description = "User's email")
    @NotBlank(message = "E-mail is required.")
    @Email(message = "Invalid e-mail format.")
    private String email;

    @Schema(example = "20354586920", description = "User's CPF")
    @NotBlank(message = "CPF is required.")
    @CPF(message = "Invalid CPF format.")
    private String cpf;

    @Schema(example = "42993896891", description = "User's phone number")
    @NotBlank(message = "Phone number is required.")
    @ValidPhoneNumber
    private String phoneNumber;

    @Schema(example = "27/04/2001", description = "User's date of birth")
    @NotNull(message = "Date of birth is required.")
    @ValidDateOfBirth(message = "Invalid date of birth. User must be between 18 and 125 years old, " +
            "and the date cannot be in the future.")
    private LocalDate dateOfBirth;

    @Schema(example = "Password@123", description = "User's password")
    @NotBlank(message = "Password is required.")
    @ValidPassword
    private String password;

    @Schema(description = "User's addresses")
    @NotNull(message = "Addresses is required.")
    @Size(min = 1, max = 20, message = "Addresses must contain between 1 and 20 items.")
    private Set<AddressCreateDTO> addresses;

}
