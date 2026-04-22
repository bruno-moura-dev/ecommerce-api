package com.brunomoura.ecommerceapi.controller;

import com.brunomoura.ecommerceapi.dto.auth.LoginRequestDTO;
import com.brunomoura.ecommerceapi.dto.auth.LoginResponseDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {

        LoginResponseDTO response = authService.login(dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register user and return JWT token")
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody @Valid UserCreateRequestDTO dto) {

        LoginResponseDTO response = authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
