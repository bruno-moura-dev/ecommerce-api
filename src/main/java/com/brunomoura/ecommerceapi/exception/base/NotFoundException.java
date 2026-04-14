package com.brunomoura.ecommerceapi.exception.base;

import com.brunomoura.ecommerceapi.enums.ErrorCode;

public class NotFoundException extends BaseException {
    public NotFoundException(ErrorCode code, String message) {
        super(code, message);
    }
}
