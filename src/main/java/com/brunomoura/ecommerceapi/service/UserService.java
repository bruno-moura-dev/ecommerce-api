package com.brunomoura.ecommerceapi.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.exception.user.InvalidPasswordException;
import com.brunomoura.ecommerceapi.exception.user.InvalidRangeDateException;
import com.brunomoura.ecommerceapi.exception.user.UserAlreadyExistsException;
import com.brunomoura.ecommerceapi.exception.user.UserNotFoundException;
import com.brunomoura.ecommerceapi.mapper.UserMapper;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import com.brunomoura.ecommerceapi.specification.UserSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserCreateResponseDTO create(UserCreateRequestDTO userCreateRequestDTO) {

        if (userRepository.existsByEmailOrCpf(userCreateRequestDTO.getEmail(), userCreateRequestDTO.getCpf())) {
            throw new UserAlreadyExistsException("User already exists.");
        }

        User user = new User(userCreateRequestDTO.getName(), userCreateRequestDTO.getEmail(),
                userCreateRequestDTO.getCpf(), userCreateRequestDTO.getPhoneNumber(),
                userCreateRequestDTO.getDateOfBirth(), passwordEncoder.encode(userCreateRequestDTO.getPassword()));

        userCreateRequestDTO.getAddresses().forEach(address -> user.addAddress(address.getLabel(),
                address.getStreetName(), address.getHouseNumber(),
                address.getNeighborhood(), address.getState(), address.getCountry(), address.getCep()));

        userRepository.save(user);

        logger.info("User created successfully. userId={}", user.getId());

        return userMapper.convertUserToCreateResponse(user);
    }

    public UserDetailsResponseDTO findActiveById(Long userId) {

        User user = getActiveUserOrThrow(userId);

        return userMapper.convertUserToDetailsResponse(user);
    }

    public List<AddressResponseDTO> findAddresses(Long userId) {

        User user = getActiveUserOrThrow(userId);

        return user.getAddresses().stream().map(userMapper::convertAddressToResponse).toList();
    }

    public Page<UserSummaryResponseDTO> search(UserFilterDTO userFilterDTO, Pageable pageable) {

        validateRangeDate(userFilterDTO.getInitialDateOfDelete(), userFilterDTO.getFinalDateOfDelete());

        validateRangeDate(userFilterDTO.getInitialDateOfCreation(), userFilterDTO.getFinalDateOfCreation());

        Specification<User> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        spec = spec.and(UserSpecification.hasId(userFilterDTO.getId()));
        spec = spec.and(UserSpecification.hasName(userFilterDTO.getName()));
        spec = spec.and(UserSpecification.hasCpf(userFilterDTO.getCpf()));
        spec = spec.and(UserSpecification.hasEmail(userFilterDTO.getEmail()));
        spec = spec.and(UserSpecification.hasRole(userFilterDTO.getRole()));
        spec = spec.and(UserSpecification.wasDeletedAt(userFilterDTO.getInitialDateOfDelete(),
                userFilterDTO.getFinalDateOfDelete()));
        spec = spec.and(UserSpecification.wasCreatedAt(userFilterDTO.getInitialDateOfCreation(),
                userFilterDTO.getFinalDateOfCreation()));


        return userRepository.findAll(spec, pageable).map(userMapper::convertUserToSummaryResponse);
    }

    @Transactional
    public UserDetailsResponseDTO update(Long userId, UserUpdateDTO userUpdateDTO) {

        User user = getActiveUserOrThrow(userId);

        if (userUpdateDTO.getName() != null) {
            user.setName(userUpdateDTO.getName());
        }

        if (userUpdateDTO.getCpf() != null) {

            if (!Objects.equals(userUpdateDTO.getCpf(), user.getCpf())) {

                if (userRepository.existsByCpfAndIdNot(userUpdateDTO.getCpf(), userId)) {
                    throw new UserAlreadyExistsException(String.format("User with CPF: %s already exists.",
                            userUpdateDTO.getCpf()));
                }

                user.setCpf(userUpdateDTO.getCpf());
            }
        }

        if (userUpdateDTO.getEmail() != null) {

            if (!Objects.equals(userUpdateDTO.getEmail(), user.getEmail())) {

                if (userRepository.existsByEmailAndIdNot(userUpdateDTO.getEmail(), user.getId())) {
                    throw new UserAlreadyExistsException(String.format("User with e-mail: %s already exists.",
                            userUpdateDTO.getEmail()));
                }

                user.setEmail(userUpdateDTO.getEmail());
            }
        }

        if (userUpdateDTO.getPhoneNumber() != null) {

            user.setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }

        if (userUpdateDTO.getDateOfBirth() != null) {

            user.setDateOfBirth(userUpdateDTO.getDateOfBirth());
        }

        logger.info("User updated successfully. userId={}", userId);

        return userMapper.convertUserToDetailsResponse(user);
    }

    @Transactional
    public void updatePassword(Long id, UserUpdatePasswordDTO userUpdatePasswordDTO) {

        User user = getActiveUserOrThrow(id);

        if (!passwordEncoder.matches(userUpdatePasswordDTO.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException("Invalid password.");
        }

        user.changePassword(userUpdatePasswordDTO.getNewPassword(),
                passwordEncoder.encode(userUpdatePasswordDTO.getNewPassword()));
    }

    @Transactional
    public AddressResponseDTO updateAddress(Long userId, Long addressId, AddressRequestDTO addressRequestDTO) {

        User user = getActiveUserOrThrow(userId);

        Address updatedAddress = user.updateAddress(addressId, addressRequestDTO.getLabel(), addressRequestDTO.getStreetName(),
                addressRequestDTO.getHouseNumber(), addressRequestDTO.getNeighborhood(), addressRequestDTO.getState(),
                addressRequestDTO.getCountry(), addressRequestDTO.getCep());

        logger.info("Address updated successfully. userId={}, addressId={}", userId, addressId);

        return userMapper.convertAddressToResponse(updatedAddress);
    }

    @Transactional
    public void removeAddress(Long userId, Long addressId) {

        User user = getActiveUserOrThrow(userId);
        user.removeAddress(addressId);

        logger.info("Address removed successfully. userId={}, addressId={}", userId, addressId);
    }

    @Transactional
    public void delete(Long userId) {

        User user = getActiveUserOrThrow(userId);
        user.deleteUser();

        logger.info("User soft deleted. userId={}", userId);
    }

    @Transactional
    public void reactivate(Long userId) {

        User user = findByIdIncludingDeleted(userId);

        user.activeUser();
    }

    private User getActiveUserOrThrow(Long userId) {

        return userRepository.findActiveById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %d not found.", userId)));
    }

    private User findByIdIncludingDeleted(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("User not found. userId=%d", userId))
        );
    }

    private void validateRangeDate(Instant initialDate, Instant finalDate) {
        if (initialDate != null && finalDate != null) {

            if (initialDate.isAfter(finalDate)) {
                throw new InvalidRangeDateException("The range date informed is not valid.");
            }
        }
    }


}
