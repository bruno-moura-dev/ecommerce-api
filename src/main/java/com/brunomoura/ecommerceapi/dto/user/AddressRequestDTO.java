package com.brunomoura.ecommerceapi.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddressRequestDTO {

    @Size(max = 50, message = "Label must be shorter than 50 characters long.")
    private String label;

    @NotBlank(message = "Street name is required.")
    @Size(max = 100, message = "Street name must be shorter than 100 characters long.")
    private String streetName;

    @Size(max = 10, message = "House number must be shorter than 10 digits long.")
    private String houseNumber;

    @Size(max = 60, message = "Neighborhood must be shorter than 60 characters long.")
    private String neighborhood;

    @NotBlank(message = "State is required.")
    @Size(max = 50, message = "State must be shorter than 50 characters long.")
    private String state;

    @NotBlank(message = "Country is required.")
    @Size(max = 60, message = "Country must be shorter than 60 characters long.")
    private String country;

    @Size(max = 8, message = "CEP must be shorter than 8 digits long.")
    private String cep;
}
