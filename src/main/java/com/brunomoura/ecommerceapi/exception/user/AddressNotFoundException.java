package com.brunomoura.ecommerceapi.exception.user;

public class AddressNotFoundException extends RuntimeException {
    public AddressNotFoundException(String message) {
        super(message);
    }
}
