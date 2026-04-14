package com.brunomoura.ecommerceapi.exception.auth;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.base.BaseException;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException(ErrorCode code, String message) {
        super(code, message);
    }
}
