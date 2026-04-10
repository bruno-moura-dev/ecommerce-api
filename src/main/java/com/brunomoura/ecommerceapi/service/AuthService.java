package com.brunomoura.ecommerceapi.service;

import com.brunomoura.ecommerceapi.dto.auth.LoginRequestDTO;
import com.brunomoura.ecommerceapi.dto.auth.LoginResponseDTO;

import com.brunomoura.ecommerceapi.dto.user.UserCreateRequestDTO;
import com.brunomoura.ecommerceapi.security.CustomUserDetailsService;
import com.brunomoura.ecommerceapi.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthService(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService,
                       CustomUserDetailsService customUserDetailsService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }

    public LoginResponseDTO login(LoginRequestDTO dto) {

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(auth);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return generateTokenForUser(userDetails);
    }

    public LoginResponseDTO register(UserCreateRequestDTO dto) {

        userService.register(dto);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(dto.getEmail());

        return generateTokenForUser(userDetails);
    }


    private LoginResponseDTO generateTokenForUser(UserDetails userDetails) {

        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(token, jwtService.extractExpiration(token).toInstant());
    }


}
