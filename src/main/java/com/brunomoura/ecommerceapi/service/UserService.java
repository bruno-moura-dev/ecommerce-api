package com.brunomoura.ecommerceapi.service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.AddressUpdate;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.auth.InvalidCredentialsException;
import com.brunomoura.ecommerceapi.exception.base.BusinessException;
import com.brunomoura.ecommerceapi.exception.user.InvalidCurrentPasswordException;
import com.brunomoura.ecommerceapi.exception.base.NotFoundException;
import com.brunomoura.ecommerceapi.mapper.UserMapper;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import com.brunomoura.ecommerceapi.specification.UserSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserCreateResponseDTO register(UserCreateRequestDTO dto) {
        return buildCreateUserMethod(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserCreateResponseDTO create(UserCreateRequestDTO dto) {
        return buildCreateUserMethod(dto);
    }

    @PreAuthorize(("hasRole('ADMIN') or #userId == principal.id"))
    @Transactional
    public AddressAddResponseDTO addAddress(Long userId, AddressRequestDTO dto) {
        User user = getActiveUserOrThrow(userId);

        Address address = user.addAddress(dto.getLabel(), dto.getStreetName(), dto.getHouseNumber(),
                dto.getNeighborhood(), dto.getCity(), dto.getState(), dto.getCountry(), dto.getZipCode());

        logger.info("Address added successfully. userId={}", userId);

        return userMapper.convertAddressToAddResponse(address);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public UserDetailsResponseDTO findActiveById(Long userId) {

        User user = getActiveUserOrThrow(userId);

        return userMapper.convertUserToDetailsResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public List<AddressDetailsResponseDTO> findAddresses(Long userId) {

        User user = getActiveUserOrThrow(userId);

        return user.getAddresses().stream().map(userMapper::convertAddressToDetailsResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserSummaryResponseDTO> search(UserFilterDTO dto, Pageable pageable) {

        validateRangeDate(dto.getInitialDateOfDelete(), dto.getFinalDateOfDelete());

        validateRangeDate(dto.getInitialDateOfCreation(), dto.getFinalDateOfCreation());

        Specification<User> spec = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        spec = spec.and(UserSpecification.hasId(dto.getId()));
        spec = spec.and(UserSpecification.hasName(dto.getName()));
        spec = spec.and(UserSpecification.hasCpf(dto.getCpf()));
        spec = spec.and(UserSpecification.hasEmail(dto.getEmail()));
        spec = spec.and(UserSpecification.hasRole(dto.getRole()));
        spec = spec.and(UserSpecification.wasDeletedAt(dto.getInitialDateOfDelete(),
                dto.getFinalDateOfDelete()));
        spec = spec.and(UserSpecification.wasCreatedAt(dto.getInitialDateOfCreation(),
                dto.getFinalDateOfCreation()));

        return userRepository.findAll(spec, pageable).map(userMapper::convertUserToSummaryResponse);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Transactional
    public UserDetailsResponseDTO update(Long userId, UserUpdateDTO dto) {

        User user = getActiveUserOrThrow(userId);

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null) {
            if (!Objects.equals(dto.getEmail(), user.getEmail())) {

                if (userRepository.existsByEmailAndIdNot(dto.getEmail(), user.getId())) {
                    throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS,
                            String.format("User with e-mail: %s already exists", dto.getEmail()));
                }

                user.setEmail(dto.getEmail());
            }
        }

        if (dto.getCpf() != null) {
            if (!Objects.equals(dto.getCpf(), user.getCpf())) {

                if (userRepository.existsByCpfAndIdNot(dto.getCpf(), user.getId())) {
                    throw new BusinessException(ErrorCode.CPF_ALREADY_EXISTS,
                            String.format("User with CPF: %s already exists", dto.getCpf()));
                }

                user.setCpf(dto.getCpf());
            }
        }

        if (dto.getPhoneNumber() != null) {
            user.setPhoneNumber(dto.getPhoneNumber());
        }

        if (dto.getDateOfBirth() != null) {
            user.setDateOfBirth(dto.getDateOfBirth());
        }

        logger.info("User updated successfully. userId={}", userId);

        return userMapper.convertUserToDetailsResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @Transactional
    public void updatePassword(Long userId, UserUpdatePasswordDTO dto) {

        User user = getActiveUserOrThrow(userId);

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            throw new InvalidCurrentPasswordException(ErrorCode.INVALID_CURRENT_PASSWORD, "Invalid current password");
        }

        user.changePassword(dto.getNewPassword(),
                passwordEncoder.encode(dto.getNewPassword()));

        logger.info("User password updated successfully. userId={}", userId);
    }

    @PreAuthorize("hasRole('ADMIN') and #userId != principal.id")
    @Transactional
    public void updateRole(Long userId, UserUpdateRoleDTO dto) {

        User user = getActiveUserOrThrow(userId);
        user.updateRole(dto.getRole());

        logger.info("Role updated successfully. userId={}", userId);
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @Transactional
    public AddressAddResponseDTO updateAddress(Long userId, Long addressId, AddressRequestDTO dto) {

        User user = getActiveUserOrThrow(userId);

        AddressUpdate addressUpdate = new AddressUpdate(
                dto.getLabel(), dto.getStreetName(), dto.getHouseNumber(), dto.getNeighborhood(), dto.getCity(),
                dto.getState(), dto.getCountry(), dto.getZipCode()
        );

        Address address = user.updateAddress(addressId, addressUpdate);

        logger.info("Address updated successfully. userId={}", userId);

        return userMapper.convertAddressToAddResponse(address);
    }

    @Transactional
    public void reactivate(ReactivateUserDTO dto) {

        User user = findByEmailIncludingDeleted(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException(ErrorCode.INVALID_CREDENTIALS,
                    "Invalid email or password");
        }

        user.activeUser();

        logger.info("User successfully reactivated. userId={}", user.getId());
    }

    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    @Transactional
    public void removeAddress(Long userId, Long addressId) {

        User user = getActiveUserOrThrow(userId);
        user.removeAddress(addressId);

        logger.info("Address removed successfully. userId={}, addressId={}", userId, addressId);
    }

    @PreAuthorize("(hasRole('ADMIN') and #userId != authentication.principal.id) " +
            "or (hasRole('USER') and #userId == authentication.principal.id)")
    @Transactional
    public void delete(Long userId) {

        User user = getActiveUserOrThrow(userId);
        user.deleteUser();

        logger.info("User soft deleted. userId={}", userId);
    }


    // INTERNAL METHODS
    private void validateUserUniquenessOrThrow(UserCreateRequestDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists");
        }
        if (userRepository.existsByCpf(dto.getCpf())) {
            throw new BusinessException(ErrorCode.CPF_ALREADY_EXISTS, "CPF already exists");
        }
    }

    private User getActiveUserOrThrow(Long userId) {

        return userRepository.findActiveById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND,
                        String.format("User with id: %d not found", userId)));
    }

    private User findByEmailIncludingDeleted(String email) {

        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User with email: %s not found", email))
        );
    }

    private User findByIdIncludingDeleted(Long userId) {

        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(ErrorCode.USER_NOT_FOUND, String.format("User not found. userId=%d", userId))
        );
    }

    private void validateRangeDate(Instant initialDate, Instant finalDate) {
        if (initialDate != null && finalDate != null) {

            if (initialDate.isAfter(finalDate)) {
                throw new BusinessException(ErrorCode.INVALID_RANGE_DATE, "The range date informed is not valid");
            }
        }
    }

    private UserCreateResponseDTO buildCreateUserMethod(UserCreateRequestDTO dto) {
        validateUserUniquenessOrThrow(dto);

        User user = new User(dto.getName(), dto.getEmail(),
                dto.getCpf(), dto.getPhoneNumber(),
                dto.getDateOfBirth(), passwordEncoder.encode(dto.getPassword()));

        dto.getAddresses().forEach(address -> user.addAddress(address.getLabel(),
                address.getStreetName(), address.getHouseNumber(),
                address.getNeighborhood(), address.getCity(), address.getState(), address.getCountry(), address.getZipCode()));

        User userSaved = userRepository.save(user);

        logger.info("User created successfully. userId={}", userSaved.getId());

        return userMapper.convertUserToCreateResponse(userSaved);
    }
}
