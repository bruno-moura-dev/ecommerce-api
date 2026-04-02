package com.brunomoura.ecommerceapi.dto.user;

import java.time.LocalDate;

public record UserDetailsResponseDTO (Long id, String name, String cpf, String phoneNumber, LocalDate dateOfBirth) {
}
