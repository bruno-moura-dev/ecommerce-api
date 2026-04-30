package com.brunomoura.ecommerceapi.dto.user;

public record AddressResponseDTO(Long id, String label, String streetName, String houseNumber, String neighborhood,
                                 String city, String state, String country, String zipCode) {
}
