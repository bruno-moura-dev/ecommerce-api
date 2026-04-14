package com.brunomoura.ecommerceapi.exception.model;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ErrorResponse {

    private Instant timestamp;

    private int status;

    private String error;

    private ErrorCode code;

    private String message;

    private String path;

    private Map<String, String> errors = new HashMap<>();

    public ErrorResponse(Instant timestamp, int status, String error, ErrorCode code, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
    }
}
