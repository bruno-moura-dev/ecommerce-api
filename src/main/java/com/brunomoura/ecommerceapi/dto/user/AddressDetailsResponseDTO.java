package com.brunomoura.ecommerceapi.dto.user;

public record AddressDetailsResponseDTO(Long id, String label, String streetName, String houseNumber,
                                        String neighborhood, String city, String state, String country, String zipCode) {
}
