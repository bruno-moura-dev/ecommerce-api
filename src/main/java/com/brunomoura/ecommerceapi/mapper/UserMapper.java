package com.brunomoura.ecommerceapi.mapper;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.utils.formatter.EmailUtils;
import com.brunomoura.ecommerceapi.utils.formatter.PhoneNumberUtils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "email", target = "email", qualifiedByName = "maskEmail")
    @Mapping(source = "phoneNumber", target = "phoneNumber", qualifiedByName = "maskPhoneNumber")
    UserCreateResponseDTO toCreateResponse(User user);

    UserDetailsResponseDTO toUserDetailsResponse(User user);

    UserSummaryResponseDTO toSummaryResponse(User user);

    AddressAddResponseDTO toAddressResponse(Address address);

    AddressDetailsResponseDTO toAddressDetailsResponse(Address address);


    @Named("maskEmail")
    default String maskEmail(String email) {

        return EmailUtils.maskEmail(email);
    }

    @Named("maskPhoneNumber")
    default String maskPhoneNumber(String phoneNumber) {

        return PhoneNumberUtils.maskPhoneNumber(phoneNumber);
    }
}
