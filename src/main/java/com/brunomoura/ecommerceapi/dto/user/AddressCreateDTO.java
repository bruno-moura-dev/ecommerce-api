package com.brunomoura.ecommerceapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressCreateDTO {

    @Schema(example = "Casa", description = "Label")
    @Size(max = 50, message = "Label must be shorter than 50 characters long.")
    private String label;

    @Schema(example = "Rua Alir Castilho de Almeida", description = "Street name")
    @NotBlank(message = "Street name is required.")
    @Size(min=2, max = 100, message = "Street name must be between 2 and 100 characters long.")
    private String streetName;

    @Schema(example = "1500", description = "House number")
    @Size(max = 10, message = "House number must be shorter than 10 digits long.")
    private String houseNumber;

    @Schema(example = "Jardim Carvalho", description = "Neighborhood")
    @Size(max = 60, message = "Neighborhood must be shorter than 60 characters long.")
    private String neighborhood;

    @Schema(example = "Ponta Grossa", description = "City")
    @NotBlank(message = "City is required.")
    @Size(max = 50, message = "City must be shorter than 50 characters long.")
    private String city;

    @Schema(example = "Paraná", description = "State")
    @NotBlank(message = "State is required.")
    @Size(max = 50, message = "State must be shorter than 50 characters long.")
    private String state;

    @Schema(example = "Brasil", description = "Country")
    @NotBlank(message = "Country is required.")
    @Size(max = 60, message = "Country must be shorter than 60 characters long.")
    private String country;

    @Schema(example = "81800000", description = "Zip code")
    @Size(max = 8, message = "Zip code must be shorter than 8 digits long.")
    private String zipCode;
}
