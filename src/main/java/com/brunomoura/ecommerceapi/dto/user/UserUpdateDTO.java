package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.validation.annotation.ValidDateOfBirth;
import com.brunomoura.ecommerceapi.validation.annotation.ValidPhoneNumber;
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

    @Size(max = 100, message = "Name must be between 2 and 100 characters long.")
    private String name;

    @Email(message = "Invalid e-mail format.")
    private String email;

    @CPF(message = "Invalid CPF format.")
    private String cpf;

    @ValidPhoneNumber
    private String phoneNumber;

    @ValidDateOfBirth
    private LocalDate dateOfBirth;

}
