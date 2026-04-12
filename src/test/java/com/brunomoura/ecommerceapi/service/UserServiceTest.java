package com.brunomoura.ecommerceapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.exception.BusinessException;
import com.brunomoura.ecommerceapi.exception.NotFoundException;
import com.brunomoura.ecommerceapi.mapper.UserMapper;
import com.brunomoura.ecommerceapi.repository.UserRepository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Nested
    class UserCreation {

        @Test
        void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
            UserCreateRequestDTO dto = createValidUserRequest();

            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(dto));
            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getCode());

            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {
            UserCreateRequestDTO dto = createValidUserRequest();

            when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
            when(userRepository.existsByCpf(dto.getCpf())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class, () -> userService.register(dto));
            assertEquals(ErrorCode.CPF_ALREADY_EXISTS, exception.getCode());

            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldCreateUserSuccessfully() {
            UserCreateRequestDTO requestDTO = createValidUserRequest();
            UserCreateResponseDTO expectedResponse = createExpectedUserResponse();

            when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
            when(userRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
            when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedPassword");
            when(userMapper.convertUserToCreateResponse(any())).thenReturn(expectedResponse);

            UserCreateResponseDTO responseDTO = userService.register(requestDTO);

            ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

            verify(userRepository, times(1)).save(argumentCaptor.capture());

            User savedUser = argumentCaptor.getValue();

            verify(userMapper, times(1)).convertUserToCreateResponse(savedUser);

            assertEquals(requestDTO.getName(), savedUser.getName());
            assertEquals(requestDTO.getEmail(), savedUser.getEmail());
            assertEquals(requestDTO.getCpf(), savedUser.getCpf());
            assertEquals(requestDTO.getPhoneNumber(), savedUser.getPhoneNumber());
            assertEquals(requestDTO.getDateOfBirth(), savedUser.getDateOfBirth());
            assertEquals("encodedPassword", savedUser.getPasswordHash());
            assertEquals(UserRole.USER, savedUser.getRole());
            assertEquals(requestDTO.getAddresses().size(), savedUser.getAddresses().size());

            AddressRequestDTO requestAddress = requestDTO.getAddresses().iterator().next();
            Address savedAddress = savedUser.getAddresses().iterator().next();

            assertEquals(requestAddress.getStreetName(), savedAddress.getStreetName());
            assertEquals(requestAddress.getZipCode(), savedAddress.getZipCode());

            assertEquals(expectedResponse, responseDTO);
        }
    }

    @Nested
    class FindActiveUserById {

        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            Long userId = 2L;

            when(userRepository.findActiveById(userId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> userService.findActiveById(userId));

            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());

            verify(userRepository, times(1)).findActiveById(userId);
            verify(userMapper, never()).convertUserToDetailsResponse(any());
        }

        @Test
        void shouldReturnUserActiveSuccessfully() {
            Long userId = 1L;
            User user = createValidUser();
            UserDetailsResponseDTO dto = createFindUserResponse();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.convertUserToDetailsResponse(user)).thenReturn(dto);

            UserDetailsResponseDTO responseDTO = userService.findActiveById(userId);

            verify(userRepository, times(1)).findActiveById(userId);
            verify(userMapper, times(1)).convertUserToDetailsResponse(user);

            assertEquals(dto, responseDTO);
        }
    }

    @Nested
    class FindAddresses {

        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            Long userId = 2L;

            when(userRepository.findActiveById(userId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> userService.findAddresses(userId));

            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());

            verify(userRepository, times(1)).findActiveById(userId);
            verify(userMapper, never()).convertAddressToResponse(any());
        }

        @Test
        void shouldReturnAddressesListSuccessfully() {
            Long userId = 1L;
            User user = createValidUser();
            AddressResponseDTO dto = createAddressResponseDTO();
            Address address = user.getAddresses().iterator().next();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.convertAddressToResponse(address)).thenReturn(dto);

            List<AddressResponseDTO> addressResponseList = userService.findAddresses(userId);

            verify(userRepository, times(1)).findActiveById(userId);
            verify(userMapper, times(user.getAddresses().size())).convertAddressToResponse(address);

            AddressResponseDTO addressResponseDTO = addressResponseList.iterator().next();

            assertEquals(dto, addressResponseDTO);
            assertEquals(1, addressResponseList.size());
        }
    }


    //INTERNAL METHODS
    private static User createValidUser() {

        User user = new  User(
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22),
                "Password@123"
                );

        user.addAddress("Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Paraná",
                "Brasil",
                "81800-000");

        return user;
    }

    private static UserCreateRequestDTO createValidUserRequest() {
        Set<AddressRequestDTO> addresses = Set.of(new AddressRequestDTO(
                "Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Paraná",
                "Brasil",
                "81800-000"
        ));

        return new UserCreateRequestDTO(
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22),
                "Password@123",
                addresses
        );
    }

    private static UserCreateResponseDTO createExpectedUserResponse() {

        return new UserCreateResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jo********@email.com.br",
                "(41) 9****-5262"
        );
    }

    private static UserDetailsResponseDTO createFindUserResponse() {

        return new UserDetailsResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22)
        );
    }


    private static AddressResponseDTO createAddressResponseDTO() {

        return new AddressResponseDTO(
                1L,
                "Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Paraná",
                "Brasil",
                "81800-000"
        );
    }

}
