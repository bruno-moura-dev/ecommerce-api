package com.brunomoura.ecommerceapi.exception;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                      HttpServletRequest request) {

        return buildMethodArgumentHandler(e, request);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e, HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.NOT_FOUND, e.getCode(), request, Level.WARN);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e, HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.BAD_REQUEST, e.getCode(), request, Level.WARN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalError(Exception e, HttpServletRequest request) {

        return buildHandlers(e, HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.UNEXPECTED_ERROR, request, Level.ERROR);
    }

    private ResponseEntity<ErrorResponse> buildHandlers(Exception e, HttpStatus status, ErrorCode code, HttpServletRequest request,
                                                        Level level) {

        ErrorResponse errorResponse = buildErrorResponse(e, status, code, request);

        buildLogger(level, e, code, status, request);

        return ResponseEntity.status(status).body(errorResponse);
    }

    private ResponseEntity<ErrorResponse> buildMethodArgumentHandler(MethodArgumentNotValidException e, HttpServletRequest request) {

        ErrorResponse errorResponse = buildErrorResponse(e, HttpStatus.BAD_REQUEST, ErrorCode.INVALID_FIELDS, request);

        Map<String, String> errors = e.getBindingResult().getFieldErrors().stream().collect(Collectors.toMap(
                FieldError::getField, fieldError ->
                        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Invalid value",
                ((msg1, msg2) -> msg1)
        ));

        errorResponse.setMessage("Validation failed");
        errorResponse.setErrors(errors);

        buildLogger(Level.WARN, e, ErrorCode.INVALID_FIELDS, HttpStatus.BAD_REQUEST, request);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpStatus status, ErrorCode code,HttpServletRequest request) {

        return new ErrorResponse(
                Instant.now(),
                status.value(),
                status.name(),
                code,
                e.getMessage(),
                request.getRequestURI()
        );
    }

    private void buildLogger(Level level, Exception e, ErrorCode code, HttpStatus status, HttpServletRequest request) {

        if (level == Level.ERROR) {

             logger.makeLoggingEventBuilder(level).log("{}: code={}, status={}, path={}, message={}, {}",
                     e.getClass().getSimpleName(), code, status.value(), request.getRequestURI(), e.getMessage(), e);
        } else {
            logger.makeLoggingEventBuilder(level).log("{}: code={}, status={}, path={}, message={}",
                    e.getClass().getSimpleName(), code, status.value(), request.getRequestURI(), e.getMessage());
        }
    }
}
