package com.brunomoura.ecommerceapi.service;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.AddressResponseDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateResponseDTO;
import com.brunomoura.ecommerceapi.dto.user.UserDetailsResponseDTO;
import com.brunomoura.ecommerceapi.exception.user.UserAlreadyExistsException;
import com.brunomoura.ecommerceapi.exception.user.UserNotFoundException;
import com.brunomoura.ecommerceapi.mapper.UserMapper;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    //region DEPENDENCIES
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    //endregion

    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    //region CONSTRUCTOR
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }
    //endregion

    //region METHODS
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
        logger.info("User with id: {} created successfully.", user.getId());

        return userMapper.convertUserToCreateResponse(user);
    }

    public UserDetailsResponseDTO findActiveById(Long id) {
        User user = getActiveUserOrThrow(id);

        return userMapper.convertUserToDetailsResponse(user);
    }

    public List<AddressResponseDTO> findAddresses(Long id) {
        User user = getActiveUserOrThrow(id);

        return user.getAddresses().stream().map(userMapper::convertAddressToResponse).toList();
    }

    // INTERNAL METHODS
    private User getActiveUserOrThrow(Long id) {

        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id: %d not found.", id)));
    }
    //endregion


}
