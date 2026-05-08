package com.brunomoura.ecommerceapi.controller;

import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error, duplicated email or duplicated CPF"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO dto) {

        UserCreateResponseDTO response = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get active user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> getById(@PathVariable Long userId) {

        UserDetailsResponseDTO response = userService.findActiveById(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search users with filters and pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<UserSummaryResponseDTO>> search(@ModelAttribute UserFilterDTO dto,
                                                               Pageable pageable) {

        Page<UserSummaryResponseDTO> response = userService.search(dto, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error, duplicated email or duplicated CPF"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> update(@PathVariable Long userId,
                                                         @RequestBody @Valid UserUpdateDTO dto) {

        UserDetailsResponseDTO response = userService.update(userId, dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error, Invalid current password or " +
                    "User deleted cannot be changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId,
                                               @RequestBody @Valid UserUpdatePasswordDTO dto) {

        userService.updatePassword(userId, dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User role updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or User deleted cannot be changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
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
            @ApiResponse(responseCode = "204", description = "User reactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/reactivate")
    public ResponseEntity<Void> reactivate(@RequestBody @Valid ReactivateUserDTO dto) {

        userService.reactivate(dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User soft deleted"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {

        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }


    // Address operations endpoints
    @Operation(summary = "Add address to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address added successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or duplicated address"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressResponseDTO> addAddress(@PathVariable Long userId,
                                                         @RequestBody @Valid AddressCreateDTO dto) {

        AddressResponseDTO response = userService.addAddress(userId, dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User addresses retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDetailsResponseDTO>> getAddresses(@PathVariable Long userId) {

        List<AddressDetailsResponseDTO> response = userService.findAddresses(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User address updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error, duplicated address or " +
                    "User deleted cannot be changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long userId, @PathVariable Long addressId,
                                                            @RequestBody @Valid AddressUpdateDTO dto) {

        AddressResponseDTO response = userService.updateAddress(userId, addressId, dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User address deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error, duplicated address or " +
                    "User deleted cannot be changed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> removeAddress(@PathVariable Long userId, @PathVariable Long addressId) {

        userService.removeAddress(userId, addressId);

        return ResponseEntity.noContent().build();
    }
}
