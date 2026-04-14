package com.brunomoura.ecommerceapi.exception.user;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.base.BaseException;

public class InvalidCurrentPasswordException extends BaseException {
    public InvalidCurrentPasswordException(ErrorCode code, String message) {
        super(code, message);
    }
}
