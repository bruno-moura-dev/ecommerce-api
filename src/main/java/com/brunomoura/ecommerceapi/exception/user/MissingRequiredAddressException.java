package com.brunomoura.ecommerceapi.exception.user;

public class MissingRequiredAddressException extends RuntimeException {
    public MissingRequiredAddressException(String message) {
        super(message);
    }
}
