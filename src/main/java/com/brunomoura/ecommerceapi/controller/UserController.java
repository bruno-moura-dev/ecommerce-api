package com.brunomoura.ecommerceapi.controller;

import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.service.UserService;
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

    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO dto) {

        UserCreateResponseDTO response = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> getById(@PathVariable Long userId) {

        UserDetailsResponseDTO response = userService.findActiveById(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getAddresses(@PathVariable Long userId) {

        List<AddressResponseDTO> response = userService.findAddresses(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserSummaryResponseDTO>> search(@ModelAttribute UserFilterDTO dto,
                                                               Pageable pageable) {

        Page<UserSummaryResponseDTO> response = userService.search(dto, pageable);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> update(@PathVariable Long userId,
                                                         @RequestBody @Valid UserUpdateDTO dto) {

        UserDetailsResponseDTO response = userService.update(userId, dto);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId,
                                               @RequestBody @Valid UserUpdatePasswordDTO dto) {

        userService.updatePassword(userId, dto);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable Long userId) {

        userService.reactivate(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long userId, @PathVariable Long addressId,
                                                            @RequestBody @Valid AddressRequestDTO dto) {

        AddressResponseDTO response = userService.updateAddress(userId, addressId, dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<Void> removeAddress(@PathVariable Long userId, @PathVariable Long addressId) {

        userService.removeAddress(userId, addressId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {

        userService.delete(userId);

        return ResponseEntity.noContent().build();
    }

}
