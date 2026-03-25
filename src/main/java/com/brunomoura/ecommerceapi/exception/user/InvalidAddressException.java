package com.brunomoura.ecommerceapi.exception.user;

public class InvalidAddressException extends RuntimeException {
    public InvalidAddressException(String message) {
        super(message);
    }
}
