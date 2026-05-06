package com.brunomoura.ecommerceapi.unit;

import com.brunomoura.ecommerceapi.dto.auth.LoginRequestDTO;
import com.brunomoura.ecommerceapi.dto.auth.LoginResponseDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateResponseDTO;
import com.brunomoura.ecommerceapi.enums.ErrorCode;
import com.brunomoura.ecommerceapi.exception.auth.InvalidCredentialsException;
import com.brunomoura.ecommerceapi.exception.base.BaseException;
import com.brunomoura.ecommerceapi.exception.base.BusinessException;
import com.brunomoura.ecommerceapi.security.details.CustomUserDetailsService;
import com.brunomoura.ecommerceapi.security.jwt.JwtService;
import com.brunomoura.ecommerceapi.service.UserService;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Date;

import static com.brunomoura.ecommerceapi.util.TestDataFactory.createValidUserRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private com.brunomoura.ecommerceapi.service.AuthService authService;


    @Nested
    class Login {

        @Test
        void shouldThrowInvalidCredentialsWhenCredentialsAreInvalid() {

            LoginRequestDTO request = new LoginRequestDTO(
                    "test@email.com",
                    "Password@123"
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );

            when(authenticationManager.authenticate(auth))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            BaseException exception = assertThrows(InvalidCredentialsException.class, () ->
                    authService.login(request));

            assertEquals(ErrorCode.INVALID_CREDENTIALS, exception.getCode());

            verify(authenticationManager).authenticate(auth);
            verify(jwtService, never()).generateToken(any());
        }

        @Test
        void shouldLoginUser() {
            LoginRequestDTO request = new LoginRequestDTO(
                    "test@email.com",
                    "Password@123"
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
            );

            Authentication authentication = mock(Authentication.class);
            UserDetails userDetails = mock(UserDetails.class);

            Date expirationDate = Date.from(Instant.now().plusSeconds(3600000));

            when(authenticationManager.authenticate(auth)).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(jwtService.generateToken(userDetails)).thenReturn("test-jwt-token");
            when(jwtService.extractExpiration(any())).thenReturn(expirationDate);

            LoginResponseDTO response = authService.login(request);

            assertEquals("test-jwt-token", response.getToken());
            assertTrue(response.getExpiresAt().isAfter(Instant.now()));
            assertTrue(response.getType().contains("Bearer "));

            verify(authenticationManager).authenticate(auth);
            verify(jwtService).generateToken(userDetails);
        }
    }

    @Nested
    class Register {

        @Test
        void shouldThrowBusinessExceptionWhenEmailAlreadyExists() {
            UserCreateRequestDTO request = createValidUserRequest();

            when(userService.register(request))
                    .thenThrow(new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email already exists."));

            BaseException exception = assertThrows(BaseException.class, () ->
                    authService.register(request));

            assertEquals(ErrorCode.EMAIL_ALREADY_EXISTS, exception.getCode());

            verify(userService).register(request);
            verify(customUserDetailsService, never()).loadUserByUsername(request.getEmail());
        }

        @Test
        void shouldThrowUsernameNotFoundExceptionWhenEmailAlreadyExists() {
            UserCreateRequestDTO request = createValidUserRequest();
            UserCreateResponseDTO response = new UserCreateResponseDTO(
                    1L,
                    request.getName(),
                    request.getEmail(),
                    request.getPhoneNumber()
            );

            when(userService.register(request)).thenReturn(response);
            when(customUserDetailsService.loadUserByUsername(request.getEmail()))
                    .thenThrow(new UsernameNotFoundException("User not found"));

            Exception exception = assertThrows(UsernameNotFoundException.class, () ->
                    authService.register(request));

            assertEquals("User not found", exception.getMessage());

            verify(userService).register(request);
            verify(customUserDetailsService).loadUserByUsername(request.getEmail());
        }
    }
}
