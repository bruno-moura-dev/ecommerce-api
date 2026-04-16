package com.brunomoura.ecommerceapi.domain.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressUpdate {

    private String label;

    private String streetName;

    private String houseNumber;

    private String neighborhood;

    private String city;

    private String state;

    private String country;

    private String zipCode;
}
