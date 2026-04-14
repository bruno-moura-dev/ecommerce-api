package com.brunomoura.ecommerceapi.exception.handler;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.auth.InvalidCredentialsException;
import com.brunomoura.ecommerceapi.exception.base.BusinessException;
import com.brunomoura.ecommerceapi.exception.model.ErrorResponse;
import com.brunomoura.ecommerceapi.exception.user.InvalidCurrentPasswordException;
import com.brunomoura.ecommerceapi.exception.base.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
                                                                      HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_REQUEST,
                "Malformed or missing request body", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                      HttpServletRequest request) {

        return buildMethodArgumentHandler(e, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e, HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.NOT_FOUND, e.getCode(), e.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e, HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.BAD_REQUEST, e.getCode(), e.getMessage(), request);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e,
                                                                  HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.UNAUTHORIZED, e.getCode(), e.getMessage(), request);
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(InvalidCurrentPasswordException e,
                                                               HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.BAD_REQUEST, e.getCode(), e.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildHandlers(Exception e, HttpStatus status, ErrorCode code, String message,
                                                        HttpServletRequest request) {

        ErrorResponse errorResponse = buildErrorResponse(e, status, code, message, request);

        buildLogger(e, code, status, request);

        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildMethodArgumentHandler(MethodArgumentNotValidException e,
                                                                     HttpServletRequest request) {

        ErrorResponse errorResponse = buildErrorResponse(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_FIELDS,
                "Invalid fields",request);

        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
                FieldError::getField, fieldError ->
                        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                ((msg1, msg2) -> msg1)
        ));

        errorResponse.setMessage("Validation failed");
        errorResponse.setErrors(errors);

        buildLogger(e, ErrorCode.INVALID_FIELDS, HttpStatus.BAD_REQUEST, request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpStatus status, ErrorCode code, String message,
                                             HttpServletRequest request) {

        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.name(),
                code,
                message,
                request.getRequestURI()
        );
    }

    private void buildLogger(Exception e, ErrorCode code, HttpStatus status, HttpServletRequest request) {

        logger.warn("{}: code={}, status={}, path={}, message={}",
                    e.getClass().getSimpleName(), code, status.value(), request.getRequestURI(), e.getMessage());
    }
}
