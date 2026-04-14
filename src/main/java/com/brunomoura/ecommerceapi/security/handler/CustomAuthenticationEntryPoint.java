package com.brunomoura.ecommerceapi.security.handler;

import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.model.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        ErrorResponse errorResponse = new ErrorResponse(
                Instant.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "UNAUTHORIZED",
                ErrorCode.UNAUTHORIZED,
                "Authentication required",
                request.getRequestURI()
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
