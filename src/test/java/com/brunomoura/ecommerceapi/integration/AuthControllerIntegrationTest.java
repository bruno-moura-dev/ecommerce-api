package com.brunomoura.ecommerceapi.integration;

import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.auth.LoginRequestDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.repository.UserRepository;

import com.brunomoura.ecommerceapi.util.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Nested
    class register {

        @Test
        void shouldRegisterUser() throws Exception {

            UserCreateRequestDTO dto = TestDataFactory.createValidUserRequest();
            String createRequestJason = objectMapper.writeValueAsString(dto);

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createRequestJason))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.type").value("Bearer "))
                    .andExpect(jsonPath("$.expiresAt").isNotEmpty());

            Optional<User> user = userRepository.findByEmail(dto.getEmail());
            User savedUser = user.orElseThrow(
                    () -> new AssertionError("User should have been saved in database")
            );

            assertTrue(passwordEncoder.matches(dto.getPassword(), savedUser.getPasswordHash()));
            assertEquals(dto.getName(), savedUser.getName());
            assertEquals(dto.getCpf(), savedUser.getCpf());
            assertEquals(UserRole.USER, savedUser.getRole());
        }

        @Test
        void shouldReturnBadRequestWhenEmailAlreadyExists() throws Exception {
            UserCreateRequestDTO createRequest = TestDataFactory.createValidUserRequest();
            String createRequestJson = objectMapper.writeValueAsString(createRequest);

            UserCreateRequestDTO duplicateEmailRequest = TestDataFactory.createValidUserRequest();
            duplicateEmailRequest.setEmail(createRequest.getEmail());
            String duplicateEmailRequestJson = objectMapper.writeValueAsString(duplicateEmailRequest);

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createRequestJson))
                    .andExpect(status().isCreated());

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(duplicateEmailRequestJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Email already exists"));
        }

        @Test
        void shouldReturnBadRequestWhenCpfAlreadyExists() throws Exception {
            UserCreateRequestDTO createRequest = TestDataFactory.createValidUserRequest();
            String createRequestJson = objectMapper.writeValueAsString(createRequest);

            UserCreateRequestDTO duplicateCpfRequest = TestDataFactory.createValidUserRequest();
            duplicateCpfRequest.setCpf(createRequest.getCpf());
            String duplicateCpfJson = objectMapper.writeValueAsString(duplicateCpfRequest);

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createRequestJson))
                    .andExpect(status().isCreated());

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(duplicateCpfJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("CPF already exists"));
        }

        @Test
        void shouldReturnBadRequestWhenNameIsNull() throws Exception {
            UserCreateRequestDTO dto = TestDataFactory.createValidUserRequest();
            dto.setName(null);

            String json = objectMapper.writeValueAsString(dto);

            performInvalidRegister(json);
        }

        @Test
        void shouldReturnBadRequestWhenPhoneNumberIsBlank() throws Exception {
            UserCreateRequestDTO dto = TestDataFactory.createValidUserRequest();
            dto.setPhoneNumber("");

            String json = objectMapper.writeValueAsString(dto);

            performInvalidRegister(json);
        }

        @Test
        void shouldReturnBadRequestWhenEmailFormatIsInvalid() throws Exception {
            UserCreateRequestDTO dto = TestDataFactory.createValidUserRequest();
            dto.setEmail("test");

            String json = objectMapper.writeValueAsString(dto);

            performInvalidRegister(json);
        }

        @Test
        void shouldReturnBadRequestWhenPasswordDoesNotMeetRequirements() throws Exception {
            UserCreateRequestDTO dto = TestDataFactory.createValidUserRequest();
            dto.setPassword("123");

            String json = objectMapper.writeValueAsString(dto);

            performInvalidRegister(json);
        }

    }

    @Nested
    class login {

        @Test
        void shouldLoginUser() throws Exception {
            UserCreateRequestDTO userCreateRequest = TestDataFactory.createValidUserRequest();
            String createRequestJson = objectMapper.writeValueAsString(userCreateRequest);

            LoginRequestDTO loginRequest = new LoginRequestDTO(
                    userCreateRequest.getEmail(),
                    userCreateRequest.getPassword()
            );
            String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createRequestJson))
                    .andExpect(status().isCreated());

            mockMvc.perform(
                    post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginRequestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.expiresAt").isNotEmpty())
                    .andExpect(jsonPath("$.type").value("Bearer "));
        }

        @Test
        void shouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
            UserCreateRequestDTO userCreateRequest = TestDataFactory.createValidUserRequest();
            String createRequestJson = objectMapper.writeValueAsString(userCreateRequest);

            LoginRequestDTO loginRequest = new LoginRequestDTO(
                    userCreateRequest.getEmail(),
                    "invalidPassword"
            );
            String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createRequestJson))
                    .andExpect(status().isCreated());

            mockMvc.perform(
                            post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(loginRequestJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }

        @Test
        void shouldReturnUnauthorizedWhenUserDoesNotExist() throws Exception {

            UserCreateRequestDTO userCreateRequest = TestDataFactory.createValidUserRequest();
            String createRequestJson = objectMapper.writeValueAsString(userCreateRequest);

            LoginRequestDTO loginRequest = new LoginRequestDTO(
                    "invalid@email",
                    userCreateRequest.getPassword()
            );
            String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(createRequestJson))
                    .andExpect(status().isCreated());

            mockMvc.perform(
                            post("/auth/login")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(loginRequestJson))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid email or password"));
        }
    }


    // Internal Methods
    private ResultActions performInvalidRegister(String json) throws Exception {

         return mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

}
