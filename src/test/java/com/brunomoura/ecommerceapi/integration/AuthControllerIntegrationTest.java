package com.brunomoura.ecommerceapi.integration;

import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.dto.user.AddressUpdateDTO;
import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.repository.UserRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

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


    @Test
    void shouldRegisterUser() throws Exception {

        UserCreateRequestDTO dto = createValidUserRequest();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer "))
                .andExpect(jsonPath("$.expiresAt").isNotEmpty());

        Optional<User> user = userRepository.findByEmail(dto.getEmail());

        assertTrue(user.isPresent());
        assertTrue(passwordEncoder.matches(dto.getPassword(), user.get().getPasswordHash()));
        assertEquals(dto.getName(), user.get().getName());
        assertEquals(dto.getCpf(), user.get().getCpf());
        assertEquals(UserRole.USER, user.get().getRole());
    }


    // Internal Methods
    private UserCreateRequestDTO createValidUserRequest() {
        Set<AddressUpdateDTO> addresses = Set.of(new AddressUpdateDTO(
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

}
