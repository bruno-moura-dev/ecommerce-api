package com.brunomoura.ecommerceapi.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressUpdateDTO {

    @Schema(example = "Trabalho", description = "Label")
    @Size(max = 50, message = "Label must be shorter than 50 characters long.")
    private String label;

    @Schema(example = "Rua Pajurazinho", description = "Street name")
    @Size(max = 100, message = "Street name must be shorter than 100 characters long.")
    private String streetName;

    @Schema(example = "232", description = "House number")
    @Size(max = 10, message = "House number must be shorter than 10 digits long.")
    private String houseNumber;

    @Schema(example = "Distrito Industrial II", description = "Neighborhood")
    @Size(max = 60, message = "Neighborhood must be shorter than 60 characters long.")
    private String neighborhood;

    @Schema(example = "Manaus", description = "City")
    @Size(max = 50, message = "City must be shorter than 50 characters long.")
    private String city;

    @Schema(example = "Amazonas", description = "State")
    @Size(max = 50, message = "State must be shorter than 50 characters long.")
    private String state;

    @Schema(example = "Brasil", description = "Country")
    @Size(max = 60, message = "Country must be shorter than 60 characters long.")
    private String country;

    @Schema(example = "69007410", description = "Zip code")
    @Size(max = 8, message = "Zip code must be shorter than 8 digits long.")
    private String zipCode;
}
