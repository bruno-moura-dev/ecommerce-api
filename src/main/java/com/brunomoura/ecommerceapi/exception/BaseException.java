package com.brunomoura.ecommerceapi.exception;

import com.brunomoura.ecommerceapi.enums.ErrorCode;

public class BaseException extends RuntimeException {

    private final ErrorCode code;

    public BaseException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
