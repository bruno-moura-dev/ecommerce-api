package com.brunomoura.ecommerceapi.security.details;

import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found."));

        if (!user.isActive()) {
            throw new UsernameNotFoundException("Bad credentials");
        }

        return new CustomUserDetails(user);
    }
}
