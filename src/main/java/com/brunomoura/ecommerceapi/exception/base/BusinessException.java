package com.brunomoura.ecommerceapi.exception.base;

import com.brunomoura.ecommerceapi.enums.ErrorCode;

public class BusinessException extends BaseException {
    public BusinessException(ErrorCode code, String message) {
        super(code, message);
    }
}
