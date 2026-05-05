package com.brunomoura.ecommerceapi.unit;

import com.brunomoura.ecommerceapi.domain.user.Address;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.*;
import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.exception.auth.InvalidCredentialsException;
import com.brunomoura.ecommerceapi.exception.base.BaseException;
import com.brunomoura.ecommerceapi.exception.base.BusinessException;
import com.brunomoura.ecommerceapi.exception.base.NotFoundException;
import com.brunomoura.ecommerceapi.exception.user.InvalidCurrentPasswordException;
import com.brunomoura.ecommerceapi.mapper.UserMapper;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import com.brunomoura.ecommerceapi.service.UserService;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    // User operations tests
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
        void shouldCreateUser() {
            User user = createValidUser();
            UserCreateRequestDTO requestDTO = createValidUserRequest();
            UserCreateResponseDTO expectedResponse = createExpectedUserCreateResponse();

            when(userRepository.existsByEmail(requestDTO.getEmail())).thenReturn(false);
            when(userRepository.existsByCpf(requestDTO.getCpf())).thenReturn(false);
            when(passwordEncoder.encode(requestDTO.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any())).thenReturn(user);
            when(userMapper.toCreateResponse(any())).thenReturn(expectedResponse);

            UserCreateResponseDTO responseDTO = userService.register(requestDTO);

            ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

            verify(userRepository).save(argumentCaptor.capture());

            User savedUser = argumentCaptor.getValue();

            verify(userMapper).toCreateResponse(user);

            assertEquals(requestDTO.getName(), savedUser.getName());
            assertEquals(requestDTO.getEmail(), savedUser.getEmail());
            assertEquals(requestDTO.getCpf(), savedUser.getCpf());
            assertEquals(requestDTO.getPhoneNumber(), savedUser.getPhoneNumber());
            assertEquals(requestDTO.getDateOfBirth(), savedUser.getDateOfBirth());
            assertEquals("encodedPassword", savedUser.getPasswordHash());
            assertEquals(UserRole.USER, savedUser.getRole());
            assertEquals(requestDTO.getAddresses().size(), savedUser.getAddresses().size());

            AddressUpdateDTO requestAddress = requestDTO.getAddresses().iterator().next();
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

            verify(userRepository).findActiveById(userId);
            verify(userMapper, never()).toUserDetailsResponse(any());
        }

        @Test
        void shouldReturnUserActive() {
            Long userId = 1L;
            User user = createValidUser();
            UserDetailsResponseDTO dto = createExpectedUserDetailsResponse();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toUserDetailsResponse(user)).thenReturn(dto);

            UserDetailsResponseDTO responseDTO = userService.findActiveById(userId);

            verify(userRepository).findActiveById(userId);
            verify(userMapper).toUserDetailsResponse(user);

            assertEquals(dto, responseDTO);
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
            verify(userMapper, never()).toSummaryResponse(any());

            assertEquals(ErrorCode.INVALID_RANGE_DATE ,exception.getCode());
        }

        @Test
        void shouldReturnUserSummaryPage() {
            UserFilterDTO dto = createValidUserFilter();
            UserSummaryResponseDTO expectedUserSummary = createExpectedUserSummaryResponse();
            Pageable pageable = PageRequest.of(0, 10);
            List<User> userList = createUserList();
            Page<User> page = new PageImpl<>(userList);

            when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
            when(userMapper.toSummaryResponse(any())).thenReturn(expectedUserSummary);

            Page<UserSummaryResponseDTO> result = userService.search(dto, pageable);

            verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
            verify(userMapper, times(userList.size())).toSummaryResponse(any());

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
            verify(userMapper, never()).toSummaryResponse(any());

            assertTrue(result.isEmpty());
            assertTrue(result.getContent().isEmpty());
        }
    }

    @Nested
    class UpdateUser {

        @Test
        void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdateDTO dto = createUpdateUserWithCompleteFields();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(dto.getEmail(), user.getId())).thenReturn(true);

            BusinessException exception = assertThrows(BusinessException.class,
                    () -> userService.update(userId, dto));

            verify(userRepository).existsByEmailAndIdNot(dto.getEmail(), user.getId());
            verify(userRepository, never()).existsByCpfAndIdNot(any(), any());
            verify(userMapper, never()).toUserDetailsResponse(any());

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

            verify(userRepository).existsByEmailAndIdNot(dto.getEmail(), user.getId());
            verify(userRepository).existsByCpfAndIdNot(dto.getCpf(), user.getId());
            verify(userMapper, never()).toUserDetailsResponse(any());

            assertEquals(ErrorCode.CPF_ALREADY_EXISTS, exception.getCode());
        }

        @Test
        void shouldUpdateAllFields() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdateDTO requestDTO = createUpdateUserWithCompleteFields();
            UserDetailsResponseDTO expectedResult = createExpectedUserDetailsResponseAfterUpdateComplete();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userRepository.existsByEmailAndIdNot(requestDTO.getEmail(), user.getId())).thenReturn(false);
            when(userRepository.existsByCpfAndIdNot(requestDTO.getCpf(), user.getId())).thenReturn(false);
            when(userMapper.toUserDetailsResponse(user)).thenReturn(expectedResult);

            UserDetailsResponseDTO result = userService.update(userId, requestDTO);

            verify(userRepository).existsByEmailAndIdNot(requestDTO.getEmail(), user.getId());
            verify(userRepository).existsByCpfAndIdNot(requestDTO.getCpf(), user.getId());
            verify(userMapper).toUserDetailsResponse(user);

            assertEquals(expectedResult, result);
        }

    }

    @Nested
    class UpdatePassword {

        @Test
        void shouldThrowInvalidCurrentPasswordExceptionWhenPasswordIsIncorrect() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdatePasswordDTO dto = createMismatchUpdatePasswordRequest();


            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())).thenReturn(false);

            BaseException exception = assertThrows(InvalidCurrentPasswordException.class, () ->
                    userService.updatePassword(userId, dto));

            verify(passwordEncoder, never()).encode(any());

            assertEquals(ErrorCode.INVALID_CURRENT_PASSWORD, exception.getCode());
        }

        @Test
        void shouldThrowBusinessExceptionIfUserIsDeleted() {
            Long userId = 1L;
            User user = createValidUser();
            user.deleteUser();
            UserUpdatePasswordDTO dto = createMismatchUpdatePasswordRequest();


            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())).thenReturn(true);

            BaseException exception = assertThrows(BusinessException.class, () ->
                    userService.updatePassword(userId, dto));

            verify(passwordEncoder).encode(dto.getNewPassword());

            assertEquals(ErrorCode.USER_DELETED_CANNOT_BE_CHANGED, exception.getCode());
        }

        @Test
        void shouldUpdateUserPassword() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdatePasswordDTO dto = createValidUpdatePasswordRequest();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())).thenReturn(true);
            when(passwordEncoder.encode(dto.getNewPassword())).thenReturn("encodedPassword");

            userService.updatePassword(userId, dto);

            verify(userRepository).findActiveById(userId);
            verify(passwordEncoder).encode(dto.getNewPassword());

            assertEquals("encodedPassword", user.getPasswordHash());
        }
    }

    @Nested
    class UpdateRole {

        @Test
        void shouldThrowBusinessExceptionIfUserIsDeleted() {
            Long userId = 1L;
            User user = createValidUser();
            user.deleteUser();
            UserUpdateRoleDTO dto = new UserUpdateRoleDTO(UserRole.ADMIN);

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

            BaseException exception = assertThrows(BusinessException.class, () ->
                    userService.updateRole(userId, dto));

            verify(userRepository).findActiveById(userId);

            assertEquals(ErrorCode.USER_DELETED_CANNOT_BE_CHANGED, exception.getCode());
        }

        @Test
        void shouldUpdateUserRole() {
            Long userId = 1L;
            User user = createValidUser();
            UserUpdateRoleDTO dto = new UserUpdateRoleDTO(UserRole.ADMIN);

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

            userService.updateRole(userId, dto);

            verify(userRepository).findActiveById(userId);

            assertEquals(UserRole.ADMIN, user.getRole());
        }
    }

    @Nested
    class Reactivate {

        @Test
        void shouldThrowInvalidCredentialsExceptionWhenCredentialsAreInvalid() {
            ReactivateUserDTO dto = new ReactivateUserDTO("test@email.com", "Password@123");
            User user = createValidUser();

            when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(false);

            BaseException exception = assertThrows(InvalidCredentialsException.class, () ->
                    userService.reactivate(dto));

            verify(userRepository).findByEmail(dto.getEmail());
            verify(passwordEncoder).matches(dto.getPassword(),
                    user.getPasswordHash());

            assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getCode());
        }

        @Test
        void shouldActivateDeletedUser() {
            ReactivateUserDTO dto = new ReactivateUserDTO("test@email.com", "Password@123");
            User user = createValidUser();
            user.deleteUser();

            when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())).thenReturn(true);

            userService.reactivate(dto);

            verify(userRepository).findByEmail(dto.getEmail());
            verify(passwordEncoder).matches(dto.getPassword(),
                    user.getPasswordHash());

            assertTrue(user.isActive());
        }
    }

    @Nested
    class DeleteUser {

        @Test
        void shouldDeleteUser() {
            Long userId = 1L;
            User user = createValidUser();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

            userService.delete(userId);

            verify(userRepository).findActiveById(userId);

            assertFalse(user.isActive());
        }
    }


    // Address operations
    @Nested
    class AddAddress {

        @Test
        void shouldThrowBusinessExceptionWhenAddressAlreadyExists() {
            Long userId = 1L;
            User user = createValidUser();
            AddressCreateDTO request = new AddressCreateDTO("Casa",
                    "Rua Augusto",
                    "2000",
                    "Vila Nova",
                    "Curiuva",
                    "Paraná",
                    "Brasil",
                    "81800-000");
            AddressResponseDTO expectedResponse = createExpectedAddressResponse();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

            BaseException exception = assertThrows(BusinessException.class, () ->
                    userService.addAddress(userId, request));

            verify(userRepository).findActiveById(userId);
            verify(userMapper, never()).toAddressResponse(any());

            assertEquals(ErrorCode.ADDRESS_ALREADY_EXISTS, exception.getCode());
        }

        @Test
        void shouldAddAddress() {
            Long userId = 1L;
            User user = createValidUser();
            AddressCreateDTO request = createValidAddressCreate();
            AddressResponseDTO expectedResponse = createExpectedAddressResponse();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toAddressResponse(any())).thenReturn(expectedResponse);

            AddressResponseDTO addressSaved = userService.addAddress(userId, request);

            verify(userRepository).findActiveById(userId);

            assertEquals(expectedResponse, addressSaved);
            assertEquals(2, user.getAddresses().size());
        }
    }

    @Nested
    class FindUserAddresses {

        @Test
        void shouldThrowNotFoundExceptionWhenUserNotFound() {
            Long userId = 2L;

            when(userRepository.findActiveById(userId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> userService.findAddresses(userId));

            assertEquals(ErrorCode.USER_NOT_FOUND, exception.getCode());

            verify(userRepository).findActiveById(userId);
            verify(userMapper, never()).toAddressDetailsResponse(any());
        }

        @Test
        void shouldReturnAddressesList() {
            Long userId = 1L;
            User user = createValidUser();
            AddressDetailsResponseDTO dto = createExpectedAddressDetailsResponse();
            Address address = user.getAddresses().iterator().next();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toAddressDetailsResponse(address)).thenReturn(dto);

            List<AddressDetailsResponseDTO> addressResponseList = userService.findAddresses(userId);

            verify(userRepository).findActiveById(userId);
            verify(userMapper, times(user.getAddresses().size())).toAddressDetailsResponse(address);

            AddressDetailsResponseDTO addressAddResponseDTO = addressResponseList.iterator().next();

            assertEquals(dto, addressAddResponseDTO);
            assertEquals(1, addressResponseList.size());
        }
    }

    @Nested
    class UpdateAddress {

        @Test
        void shouldThrowBusinessExceptionIfUserIsDeleted() {
            Long userId = 1L;
            Long addressId = 2L;
            User user = createValidUser();
            user.deleteUser();
            AddressUpdateDTO sameAddress = new AddressUpdateDTO(
                    "Casa",
                    "Rua Augusto",
                    "2000",
                    "Vila Nova",
                    "Curiuva",
                    "Paraná",
                    "Brasil",
                    "81800-000");

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

            BaseException exception = assertThrows(BusinessException.class, () ->
                    userService.updateAddress(userId, addressId, sameAddress));

            verify(userRepository).findActiveById(userId);
            verify(userMapper, never()).toAddressResponse(any());

            assertEquals(ErrorCode.USER_DELETED_CANNOT_BE_CHANGED, exception.getCode());
        }

        @Test
        void shouldUpdateAddress() {
            Long userId = 1L;
            Long addressId = 1L;
            User user = createValidUser();

            AddressUpdateDTO request = createValidAddressRequest();
            AddressResponseDTO expectedResponse = createExpectedAddressResponseAfterUpdate();

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));
            when(userMapper.toAddressResponse(any())).thenReturn(expectedResponse);

            AddressResponseDTO updatedAddress = userService.updateAddress(userId, addressId, request);

            verify(userRepository).findActiveById(userId);
            verify(userMapper).toAddressResponse(any());

            assertEquals(expectedResponse, updatedAddress);
        }
    }

    @Nested
    class RemoveAddress {

        @Test
        void shouldRemoveAddress() {
            Long userId = 1L;
            Long addressId = 1L;
            User user = createValidUser();
            Address newAddress = user.addAddress("Casa",
                    "Rua Pedro Magalhães",
                    "120",
                    "Pinheirinho",
                    "Curitiba",
                    "Paraná",
                    "Brasil",
                    "81910-420");

            ReflectionTestUtils.setField(newAddress, "id", 2L);

            when(userRepository.findActiveById(userId)).thenReturn(Optional.of(user));

            userService.removeAddress(userId, addressId);

            verify(userRepository).findActiveById(userId);

            Optional<Address> address = user.getAddresses().stream().filter(add ->
                    add.getId().equals(addressId)).findFirst();

            assertTrue(address.isEmpty());
        }
    }

    //INTERNAL METHODS
    private User createValidUser() {

        User user = new  User(
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22),
                "Password@123"
                );

        Address address = user.addAddress("Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curiuva",
                "Paraná",
                "Brasil",
                "81800000");

        ReflectionTestUtils.setField(address, "id", 1L);

        return user;
    }

    private UserCreateRequestDTO createValidUserRequest() {
        Set<AddressUpdateDTO> addresses = Set.of(new AddressUpdateDTO(
                "Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curiuva",
                "Paraná",
                "Brasil",
                "81800000"
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

    private UserCreateResponseDTO createExpectedUserCreateResponse() {

        return new UserCreateResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jo********@email.com.br",
                "(41) 9****-5262"
        );
    }

    private UserDetailsResponseDTO createExpectedUserDetailsResponse() {

        return new UserDetailsResponseDTO(
                1L,
                "Jorge Antonio Erick",
                "jorge@email.com.br",
                "59974321905",
                "41995925262",
                LocalDate.of(2000, 10,22)
        );
    }

    private AddressResponseDTO createExpectedAddressResponse() {

        return new AddressResponseDTO(
                1L,
                "Casa",
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curitiba",
                "Paraná",
                "Brasil",
                "81800000"
        );
    }

    private AddressDetailsResponseDTO createExpectedAddressDetailsResponse() {

        return new AddressDetailsResponseDTO(
                1L,
                "Rua Augusto",
                "2000",
                "Vila Nova",
                "Curitiba",
                "City",
                "Paraná",
                "Brasil",
                "81800000"
        );
    }

    private AddressCreateDTO createValidAddressCreate() {
        return new AddressCreateDTO(
                "Casa",
                "Rua José",
                "333",
                "Hauer",
                "Curitiba",
                "Paraná",
                "Brasil",
                "81000200");
    }

    private AddressUpdateDTO createValidAddressRequest() {
        return new AddressUpdateDTO(
                "Casa",
                "Rua Augusto",
                "2010",
                "Vila Nova",
                "Curitiba",
                "Paraná",
                "Brasil",
                "81800000");
    }

    private AddressResponseDTO createExpectedAddressResponseAfterUpdate() {

        return new AddressResponseDTO(
                1L,
                "Casa",
                "Rua Augusto",
                "2010",
                "Vila Nova",
                "Curitiba",
                "Paraná",
                "Brasil",
                "81800000");
    }

    private UserFilterDTO createInvalidUserFilter() {
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

    private UserFilterDTO createValidUserFilter() {
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

    private List<User> createUserList() {
        List<User> users = new ArrayList<>();
        users.add(createValidUser());

        return users;
    }

    private UserSummaryResponseDTO createExpectedUserSummaryResponse() {

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

    private UserUpdateDTO createUpdateUserWithCompleteFields() {

        return new UserUpdateDTO(
                "Diego Maradona",
                "diego@email.com.br",
                "81380831237",
                "41995925262",
                LocalDate.of(2000, 10,22)
        );
    }

    private UserUpdateDTO createUpdateWithOnlyEmailField() {

        return new UserUpdateDTO(
                null,
                "diego@email.com.br",
                null,
                null,
                null
        );
    }

    private UserDetailsResponseDTO createExpectedUserDetailsResponseAfterUpdateComplete() {

        return new UserDetailsResponseDTO(
                1L,
                "Diego Maradona",
                "diego@email.com.br",
                "81380831237",
                "41993618252",
                LocalDate.of(2000, 10,22)
        );
    }

    private UserUpdatePasswordDTO createMismatchUpdatePasswordRequest() {

        return new UserUpdatePasswordDTO(
                "Test@123",
                "Password@123"
        );
    }

    private UserUpdatePasswordDTO createValidUpdatePasswordRequest() {

        return new UserUpdatePasswordDTO(
                "Password@123",
                "Test@123"
        );
    }

}