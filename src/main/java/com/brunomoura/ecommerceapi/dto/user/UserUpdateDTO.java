package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.validation.annotation.ValidDateOfBirth;
import com.brunomoura.ecommerceapi.validation.annotation.ValidPhoneNumber;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @Schema(example = "Fátima Gonçalves", description = "User's name")
    @Size(max = 100, message = "Name must be between 2 and 100 characters long.")
    private String name;

    @Schema(example = "fatima@email.com", description = "User's email")
    @Email(message = "Invalid e-mail format.")
    @Size(min = 1, message = "Invalid email format.")
    private String email;

    @Schema(example = "78121427010", description = "User's CPF")
    @CPF(message = "Invalid CPF format.")
    private String cpf;

    @Schema(example = "4199999-8080", description = "User's phone number")
    @ValidPhoneNumber
    private String phoneNumber;

    @Schema(example = "01/10/2003", description = "User's date of birth")
    @ValidDateOfBirth
    private LocalDate dateOfBirth;

}
