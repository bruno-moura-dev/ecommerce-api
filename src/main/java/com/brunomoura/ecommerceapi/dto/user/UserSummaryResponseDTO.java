package com.brunomoura.ecommerceapi.dto.user;

import com.brunomoura.ecommerceapi.enums.UserRole;

import java.time.Instant;
import java.time.LocalDate;

public record UserSummaryResponseDTO (Long id, String name, String cpf, String email, String phoneNumber,
                                      LocalDate dateOfBirth, UserRole role, Instant deletedAt, Instant createdAt) {

}
