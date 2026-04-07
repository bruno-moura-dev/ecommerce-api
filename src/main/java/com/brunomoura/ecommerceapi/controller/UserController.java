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
    public ResponseEntity<UserCreateResponseDTO> create(@RequestBody @Valid UserCreateRequestDTO userCreateRequestDTO) {

        UserCreateResponseDTO userCreateResponseDTO = userService.create(userCreateRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(userCreateResponseDTO);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> getById(@PathVariable Long userId) {

        UserDetailsResponseDTO userDetailsResponseDTO = userService.findActiveById(userId);

        return ResponseEntity.ok(userDetailsResponseDTO);
    }

    @GetMapping("/{userId}/addresses")
    public ResponseEntity<List<AddressResponseDTO>> getAddresses(@PathVariable Long userId) {

        List<AddressResponseDTO> addressResponseDTOList = userService.findAddresses(userId);

        return ResponseEntity.ok(addressResponseDTOList);
    }

    @GetMapping
    public ResponseEntity<Page<UserSummaryResponseDTO>> search(@ModelAttribute UserFilterDTO userFilterDTO,
                                                               Pageable pageable) {

        Page<UserSummaryResponseDTO> userSummaryResponseDTOPage = userService.search(userFilterDTO, pageable);

        return ResponseEntity.ok(userSummaryResponseDTOPage);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDetailsResponseDTO> update(@PathVariable Long userId,
                                                         @RequestBody @Valid UserUpdateDTO userUpdateDTO) {

        UserDetailsResponseDTO userDetailsResponseDTO = userService.update(userId, userUpdateDTO);

        return ResponseEntity.ok(userDetailsResponseDTO);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long userId,
                                               @RequestBody @Valid UserUpdatePasswordDTO userUpdatePasswordDTO) {

        userService.updatePassword(userId, userUpdatePasswordDTO);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/reactivate")
    public ResponseEntity<Void> reactivate(@PathVariable Long userId) {

        userService.reactivate(userId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{userId}/addresses/{addressId}")
    public ResponseEntity<AddressResponseDTO> updateAddress(@PathVariable Long userId, @PathVariable Long addressId,
                                                            @RequestBody @Valid AddressRequestDTO addressRequestDTO) {

        AddressResponseDTO addressResponseDTO = userService.updateAddress(userId, addressId, addressRequestDTO);

        return ResponseEntity.ok(addressResponseDTO);
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
