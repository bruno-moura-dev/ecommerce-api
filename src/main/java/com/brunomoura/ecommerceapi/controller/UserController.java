package com.brunomoura.ecommerceapi.controller;

import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.service.UserService;
import com.brunomoura.ecommerceapi.exception.model.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // User operations endpoints
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserCreateResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "Fátima Antonella Gonçalves",
                                                "email": "fa********@email.com",
                                                "phoneNumber": "(42) 9****-6891"
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
                                                "code": "INVALID_FIELDS",
                                                "message": "Invalid fields",
                                                "path": "/users",
                                                "errors": {
                                                        "name": "Name is required",
                                                        "email": "Email is required"
                                                }
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO dto) {
        UserCreateResponseDTO response = userService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get active user by ID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "label": "Casa",
                                                "streetName": "Rua Alir Castilho de Almeida",
                                                "houseNumber": "1500",
                                                "neighborhood": "Jardim Carvalho",
                                                "city": "Ponta Grossa",
                                                "state": "Paraná",
                                                "country": "Brasil",
                                                "zipCode": "81800000"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """)))
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> getById(@PathVariable Long userId) {
        UserDetailsResponseDTO response = userService.findActiveById(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search users with filters and pagination")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSummaryResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "Fátima",
                                                "email": "fatima_goncalves@email.com",
                                                "cpf": "20354586920",
                                                "role": "ADMIN",
                                                "initialDateOfDelete": "2026-05-10T22:00:43.744Z",
                                                "finalDateOfDelete": "2026-05-10T22:00:43.744Z",
                                                "initialDateOfCreation": "2026-05-10T22:00:43.744Z",
                                                "finalDateOfCreation": "2026-05-10T22:00:43.744Z"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/search",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/search",
                                                "errors": {}
                                            }
                                            """)))
    })
    @GetMapping("/search")
    public ResponseEntity<Page<UserSummaryResponseDTO>> search(@ModelAttribute UserFilterDTO dto, Pageable pageable) {
        Page<UserSummaryResponseDTO> response = userService.search(dto, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDetailsResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "name": "Fátima Gonçalves",
                                                "email": "fatima@email.com",
                                                "cpf": "78121427010",
                                                "phoneNumber": "4199999-8080",
                                                "dateOfBirth": "2026-05-10"
                                            }
                                            """
                            ))),

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
                                                "code": "CPF_ALREADY_EXISTS",
                                                "message": "CPF already exists",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> update(@PathVariable Long userId,
                                                         @RequestBody @Valid UserUpdateDTO dto) {
        UserDetailsResponseDTO response = userService.update(userId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user password")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User password updated successfully",
                    content = @Content),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error, invalid current password or user deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 400,
                                                "error": "BAD_REQUEST",
                                                "code": "INVALID_CURRENT_PASSWORD",
                                                "message": "Invalid current password",
                                                "path": "/users/1/password",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/password",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/password",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/password",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId,
                                               @RequestBody @Valid UserUpdatePasswordDTO dto) {
        userService.updatePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user role")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User role updated successfully",
                    content = @Content),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or user deleted cannot be changed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 400,
                                                "error": "BAD_REQUEST",
                                                "code": "USER_DELETED_CANNOT_BE_CHANGED",
                                                "message": "User deleted cannot be changed",
                                                "path": "/users/1/update-role",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/update-role",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/update-role",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/update-role",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PatchMapping("/{userId}/update-role")
    public ResponseEntity<Void> updateRole(@PathVariable Long userId, @RequestBody @Valid UserUpdateRoleDTO dto) {
        userService.updateRole(userId, dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivate a previously deactivated user",
            description = "Public endpoint. Requires email and current password. " +
                    "Intended for users who soft-deleted their own account.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User reactivated successfully",
                    content = @Content),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 400,
                                                "error": "BAD_REQUEST",
                                                "code": "USER_DELETED_CANNOT_BE_CHANGED",
                                                "message": "User deleted cannot be changed",
                                                "path": "/users/reactivate",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized or invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/reactivate",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/reactivate",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/reactivate",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PatchMapping("/reactivate")
    public ResponseEntity<Void> reactivate(@RequestBody @Valid ReactivateUserDTO dto) {
        userService.reactivate(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User soft deleted",
                    content = @Content),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/user/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1",
                                                "errors": {}
                                            }
                                            """)))
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.delete(userId);
        return ResponseEntity.noContent().build();
    }

    // Address operations endpoints
    @Operation(summary = "Add address to user")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Address added successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "label": "Casa",
                                                "streetName": "Rua Alir Castilho de Almeida",
                                                "houseNumber": "1500",
                                                "neighborhood": "Jardim Carvalho",
                                                "city": "Ponta Grossa",
                                                "state": "Paraná",
                                                "country": "Brasil",
                                                "zipCode": "81800000"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or duplicated address",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 400,
                                                "error": "BAD_REQUEST",
                                                "code": "ADDRESS_ALREADY_EXISTS",
                                                "message": "Address already exists",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressResponseDTO> addAddress(@PathVariable Long userId,
                                                         @RequestBody @Valid AddressCreateDTO dto) {
        AddressResponseDTO response = userService.addAddress(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user addresses")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User addresses retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressDetailsResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "id": 1,
                                                "label": "Casa",
                                                "streetName": "Rua Alir Castilho de Almeida",
                                                "houseNumber": "1500",
                                                "neighborhood": "Jardim Carvalho",
                                                "city": "Ponta Grossa",
                                                "state": "Paraná",
                                                "country": "Brasil",
                                                "zipCode": "81800000"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401", description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/addresses",
                                                "errors": {}
                                            }
                                            """)))
    })
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDetailsResponseDTO>> getAddresses(@PathVariable Long userId) {
        List<AddressDetailsResponseDTO> response = userService.findAddresses(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user address")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User address updated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressResponseDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "label": "Trabalho",
                                                "streetName": "Rua Pajurazinho",
                                                "houseNumber": "232",
                                                "neighborhood": "Distrito Industrial II",
                                                "city": "Manaus",
                                                "state": "Amazonas",
                                                "country": "Brasil",
                                                "zipCode": "69007410"
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error, duplicated address or user deleted",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 400,
                                                "error": "BAD_REQUEST",
                                                "code": "ADDRESS_ALREADY_EXISTS",
                                                "message": "Address already exists",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """)))
    })
    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long userId,
                                                            @PathVariable Long addressId,
                                                            @RequestBody @Valid AddressUpdateDTO dto) {
        AddressResponseDTO response = userService.updateAddress(userId, addressId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user address")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User address deleted successfully",
                    content = @Content),

            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error or user deleted cannot be changed",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 400,
                                                "error": "BAD_REQUEST",
                                                "code": "LAST_ADDRESS_REMOVAL_NOT_ALLOWED",
                                                "message": "Addresses must contain one address at least",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 401,
                                                "error": "UNAUTHORIZED",
                                                "code": "UNAUTHORIZED",
                                                "message": "Authentication required",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 403,
                                                "error": "FORBIDDEN",
                                                "code": "ACCESS_DENIED",
                                                "message": "Access denied",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """))),

            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "timestamp": "2026-05-09T14:00:00Z",
                                                "status": 404,
                                                "error": "NOT_FOUND",
                                                "code": "USER_NOT_FOUND",
                                                "message": "User not found",
                                                "path": "/users/1/addresses/1",
                                                "errors": {}
                                            }
                                            """)))
    })
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> removeAddress(@PathVariable Long userId, @PathVariable Long addressId) {
        userService.removeAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}