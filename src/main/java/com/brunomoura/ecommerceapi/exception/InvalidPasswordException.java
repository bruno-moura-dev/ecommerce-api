package com.brunomoura.ecommerceapi.exception;

import com.brunomoura.ecommerceapi.enums.ErrorCode;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(ErrorCode code, String message) {
        super(message);
    }
}
