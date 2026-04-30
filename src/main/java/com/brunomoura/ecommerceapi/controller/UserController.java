package com.brunomoura.ecommerceapi.controller;

import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO dto) {

        UserCreateResponseDTO response = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get active user by ID")
    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> getById(@PathVariable Long userId) {

        UserDetailsResponseDTO response = userService.findActiveById(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Search users with filters and pagination")
    @GetMapping
    public ResponseEntity<Page<UserSummaryResponseDTO>> search(@ModelAttribute UserFilterDTO dto,
                                                               Pageable pageable) {

        Page<UserSummaryResponseDTO> response = userService.search(dto, pageable);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> update(@PathVariable Long userId,
                                                         @RequestBody @Valid UserUpdateDTO dto) {

        UserDetailsResponseDTO response = userService.update(userId, dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user password")
    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId,
                                               @RequestBody @Valid UserUpdatePasswordDTO dto) {

        userService.updatePassword(userId, dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user role")
    @PatchMapping("/{userId}/update-role")
    public ResponseEntity<Void> updateRole(@PathVariable Long userId, @RequestBody @Valid UserUpdateRoleDTO dto) {

        userService.updateRole(userId, dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivate a previously deactivated user")
    @PatchMapping("/reactivate")
    public ResponseEntity<Void> reactivate(@RequestBody @Valid ReactivateUserDTO dto) {

        userService.reactivate(dto);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {

        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }


    // Address operations endpoints
    @Operation(summary = "Add address to user")
    @PostMapping("/{userId}/addresses")
    public ResponseEntity<AddressResponseDTO> addAddress(@PathVariable Long userId,
                                                         @RequestBody @Valid AddressCreateDTO dto) {

        AddressResponseDTO response = userService.addAddress(userId, dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user addresses")
    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressDetailsResponseDTO>> getAddresses(@PathVariable Long userId) {

        List<AddressDetailsResponseDTO> response = userService.findAddresses(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user address")
    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long userId, @PathVariable Long addressId,
                                                            @RequestBody @Valid AddressUpdateDTO dto) {

        AddressResponseDTO response = userService.updateAddress(userId, addressId, dto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete user address")
    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> removeAddress(@PathVariable Long userId, @PathVariable Long addressId) {

        userService.removeAddress(userId, addressId);

        return ResponseEntity.noContent().build();
    }
}
