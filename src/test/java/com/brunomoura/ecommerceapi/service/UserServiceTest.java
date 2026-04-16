package com.brunomoura.ecommerceapi.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.exception.base.BusinessException;
import com.brunomoura.ecommerceapi.exception.base.NotFoundException;
import com.brunomoura.ecommerceapi.mapper.UserMapper;
import com.brunomoura.ecommerceapi.repository.UserRepository;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
            User user = createValidUser();
            UserCreateRequestDTO requestDTO = createValidUserRequest();
            UserCreateResponseDTO expectedResponse = createExpectedUserCreateResponse();

            when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
            when(userRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
            when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any())).thenReturn(user);
            when(userMapper.convertUserToCreateResponse(any())).thenReturn(expectedResponse);

            UserCreateResponseDTO responseDTO = userService.register(requestDTO);

            ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

            verify(userRepository, times(1)).save(argumentCaptor.capture());

            User savedUser = argumentCaptor.getValue();

            verify(userMapper, times(1)).convertUserToCreateResponse(user);

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
            UserDetailsResponseDTO dto = createExpectedUserDetailsResponse();

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
            verify(userMapper, never()).convertAddressToDetailsResponse(any());
        }

        @Test
        void shouldReturnAddressesListSuccessfully() {
            Long userId = 1L;
            User user = createValidUser();
            AddressDetailsResponseDTO dto = createExpectedAddressDetailsResponse();
            Address address = user.getAddresses().iterator().next();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.convertAddressToDetailsResponse(address)).thenReturn(dto);

            List<AddressDetailsResponseDTO> addressResponseList = userService.findAddresses(userId);

            verify(userRepository, times(1)).findActiveById(userId);
            verify(userMapper, times(user.getAddresses().size())).convertAddressToDetailsResponse(address);

            AddressDetailsResponseDTO addressAddResponseDTO = addressResponseList.iterator().next();

            assertEquals(dto, addressAddResponseDTO);
            assertEquals(1, addressResponseList.size());
        }
    }

    @Nested
    class SearchUser {

        @Test
        void shouldThrowBusinessExceptionWhenDateRangeIsInvalid() {
            UserFilterDTO dto = createInvalidUserFilter();
            Pageable pageable = PageRequest.of(0, 1);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.search(dto, pageable));

            verify(userRepository, never()).findAll();
            verify(userMapper, never()).convertUserToSummaryResponse(any());

            assertEquals(ErrorCode.INVALID_RANGE_DATE ,exception.getCode());
        }

        @Test
        void shouldReturnUserSummaryPageSuccessfully() {
            UserFilterDTO dto = createValidUserFilter();
            UserSummaryResponseDTO expectedUserSummary = createExpectedUserSummaryResponse();
            Pageable pageable = PageRequest.of(0, 10);
            List<User> userList = createUserList();
            Page<User> page = new PageImpl<>(userList);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(userMapper.convertUserToSummaryResponse(any())).thenReturn(expectedUserSummary);

            Page<UserSummaryResponseDTO> result = userService.search(dto, pageable);

            verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
            verify(userMapper, times(userList.size())).convertUserToSummaryResponse(any());

            assertEquals(expectedUserSummary, result.getContent().get(0));
            assertEquals(userList.size(), result.getNumberOfElements());
        }

        @Test
        void shouldReturnEmptyPageWhenNoUsersFound() {
            UserFilterDTO dto = createValidUserFilter();
            Pageable pageable = PageRequest.of(0, 10);
            Page<User> page = new PageImpl<>(List.of());

            when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

            Page<UserSummaryResponseDTO> result = userService.search(dto, pageable);

            verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
            verify(userMapper, never()).convertUserToSummaryResponse(any());

            assertTrue(result.isEmpty());
            assertTrue(result.getContent().isEmpty());
        }
    }

    @Nested
    class UpdateUser {

        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            Long userId = 1L;
            UserUpdateDTO dto = createUpdateUserWithCompleteFields();


            when(userRepository.findActiveById(userId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> userService.update(userId, dto));

            verify(userRepository, never()).existsByEmailAndIdNot(any(), any());
            verify(userRepository, never()).existsByCpfAndIdNot(any(), any());
            verify(userMapper, never()).convertUserToDetailsResponse(any());

            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());
        }

        @Test
        void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdateDTO dto = createUpdateUserWithCompleteFields();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(dto.getEmail(), user.getId())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.update(userId, dto));

            verify(userRepository, times(1)).existsByEmailAndIdNot(dto.getEmail(), user.getId());
            verify(userRepository, never()).existsByCpfAndIdNot(any(), any());
            verify(userMapper, never()).convertUserToDetailsResponse(any());

            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getCode());
        }

        @Test
        void shouldThrowBusinessExceptionWhenCpfAlreadyExists() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdateDTO dto = createUpdateUserWithCompleteFields();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(dto.getEmail(), user.getId())).thenReturn(false);
            when(userRepository.existsByCpfAndIdNot(dto.getCpf(), user.getId())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.update(userId, dto));

            verify(userRepository, times(1)).existsByEmailAndIdNot(dto.getEmail(), user.getId());
            verify(userRepository, times(1)).existsByCpfAndIdNot(dto.getCpf(), user.getId());
            verify(userMapper, never()).convertUserToDetailsResponse(any());

            assertEquals(ErrorCode.CPF_ALREADY_EXISTS, exception.getCode());
        }

        @Test
        void shouldUpdateAllFieldsSuccessfully() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdateDTO requestDTO = createUpdateUserWithCompleteFields();
            UserDetailsResponseDTO expectedResult = createExpectedUserDetailsResponseAfterUpdateComplete();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(requestDTO.getEmail(), user.getId())).thenReturn(false);
            when(userRepository.existsByCpfAndIdNot(requestDTO.getCpf(), user.getId())).thenReturn(false);
            when(userMapper.convertUserToDetailsResponse(user)).thenReturn(expectedResult);

            UserDetailsResponseDTO result = userService.update(userId, requestDTO);

            verify(userRepository, times(1))
                    .existsByEmailAndIdNot(requestDTO.getEmail(), user.getId());
            verify(userRepository, times(1))
                    .existsByCpfAndIdNot(requestDTO.getCpf(), user.getId());
            verify(userMapper, times(1)).convertUserToDetailsResponse(user);

            assertEquals(expectedResult, result);
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
                "Curiuva",
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
                "Curiuva",
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

    private static UserCreateResponseDTO createExpectedUserCreateResponse() {

        return new UserCreateResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jo********@email.com.br",
                "(41) 9****-5262"
        );
    }

    private static UserDetailsResponseDTO createExpectedUserDetailsResponse() {

        return new UserDetailsResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22)
        );
    }

    private static AddressAddResponseDTO createExpectedAddAddressResponse() {

        return new AddressAddResponseDTO(
                1L,
                "Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curitiba",
                "Paraná",
                "Brasil",
                "81800-000"
        );
    }

    private static AddressDetailsResponseDTO createExpectedAddressDetailsResponse() {

        return new AddressDetailsResponseDTO(
                1L,
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curitiba",
                "City",
                "Paraná",
                "Brasil",
                "81800-000"
        );
    }

    private static UserFilterDTO createInvalidUserFilter() {
        return new UserFilterDTO(
                1L,
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                UserRole.USER,
                Instant.now().plusSeconds(3600000),
                Instant.now(),
                Instant.now(),
                Instant.now().plusSeconds(3600000)
        );
    }

    private static UserFilterDTO createValidUserFilter() {
        return new UserFilterDTO(
                1L,
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                UserRole.USER,
                Instant.now(),
                Instant.now().plusSeconds(3600000),
                Instant.now(),
                Instant.now().plusSeconds(3600000)
        );
    }

    private static List<User> createUserList() {
        List<User> users = new ArrayList<>();
        users.add(createValidUser());

        return users;
    }

    private static UserSummaryResponseDTO createExpectedUserSummaryResponse() {

        return new UserSummaryResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22),
                UserRole.USER,
                null,
                Instant.now()
        );
    }

    private static UserUpdateDTO createUpdateUserWithCompleteFields() {

        return new UserUpdateDTO(
                "Diego Maradona",
                "diego@email.com.br",
                "81380831237",
                "41995925262",
                LocalDate.of(2000, 10,22)
        );
    }

    private static UserUpdateDTO createUpdateWithOnlyEmailField() {

        return new UserUpdateDTO(
                null,
                "diego@email.com.br",
                null,
                null,
                null
        );
    }

    private static UserDetailsResponseDTO createExpectedUserDetailsResponseAfterUpdateComplete() {

        return new UserDetailsResponseDTO(
                1L,
                "Diego Maradona",
                "diego@email.com.br",
                "81380831237",
                "41993618252",
                LocalDate.of(2000, 10,22)
        );
    }
}