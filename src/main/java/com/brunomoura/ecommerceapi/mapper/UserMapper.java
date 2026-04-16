package com.brunomoura.ecommerceapi.mapper;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.utils.formatter.EmailUtils;
import com.brunomoura.ecommerceapi.utils.formatter.PhoneNumberUtils;

import org.springframework.stereotype.Component;


@Component
public class UserMapper {

    public UserCreateResponseDTO convertUserToCreateResponse(User user) {

        return new UserCreateResponseDTO(
                user.getId(),
                user.getName(),
                EmailUtils.maskEmail(user.getEmail()),
                PhoneNumberUtils.maskPhoneNumber(user.getPhoneNumber())
        );
    }

    public UserDetailsResponseDTO convertUserToDetailsResponse(User user) {

        return new UserDetailsResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getPhoneNumber(),
                user.getDateOfBirth()
        );
    }

    public UserSummaryResponseDTO convertUserToSummaryResponse(User user) {

        return new UserSummaryResponseDTO(
                user.getId(),
                user.getName(),
                user.getCpf(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getRole(),
                user.getDeletedAt(),
                user.getCreatedAt()
        );
    }

    public AddressAddResponseDTO convertAddressToAddResponse(Address address) {

        return new AddressAddResponseDTO(
                address.getLabel(),
                address.getStreetName(),
                address.getHouseNumber(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipCode());
    }

    public AddressDetailsResponseDTO convertAddressToDetailsResponse(Address address) {

        return new AddressDetailsResponseDTO(
                address.getId(),
                address.getLabel(),
                address.getStreetName(),
                address.getHouseNumber(),
                address.getNeighborhood(),
                address.getCity(),
                address.getState(),
                address.getCountry(),
                address.getZipCode());
    }

}
