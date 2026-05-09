package com.brunomoura.ecommerceapi.controller;

import com.brunomoura.ecommerceapi.dto.auth.LoginRequestDTO;
import com.brunomoura.ecommerceapi.dto.auth.LoginResponseDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.exception.model.ErrorResponse;
import com.brunomoura.ecommerceapi.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Authenticate user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIswiZXhwIjoxNzQ2ODAzNjAwfQ.dGhpcy1pcy1hLWZha2UtZXhhbXBsZS10b2tlbg",
                                                "expiresAt": "2026-05-10T22:13:54.915Z",
                                                "type": "Bearer"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid email or password",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "timestamp": "2026-05-09T14:00:00Z",
                                        "status": 401,
                                        "error": "UNAUTHORIZED",
                                        "code": "INVALID_CREDENTIALS",
                                        "message": "Invalid email or password",
                                        "path": "/auth/login",
                                        "errors": {}
                                    }
                                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {

        LoginResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register user and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkB0ZXN0LmNvbSIswiZXhwIjoxNzQ2ODAzNjAwfQ.dGhpcy1pcy1hLWZha2UtZXhhbXBsZS10b2tlbg",
                                                "expiresAt": "2026-05-10T22:13:54.915Z",
                                                "type": "Bearer"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error, duplicated email or duplicated CPF",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                        "timestamp": "2026-05-09T14:00:00Z",
                                        "status": 400,
                                        "error": "BAD_REQUEST",
                                        "code": "EMAIL_ALREADY_EXISTS",
                                        "message": "Email already exists",
                                        "path": "/auth/register",
                                        "errors": {}
                                    }
                                    """)))
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody @Valid UserCreateRequestDTO dto) {

        LoginResponseDTO response = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
