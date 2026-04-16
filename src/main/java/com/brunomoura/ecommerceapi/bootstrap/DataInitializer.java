package com.brunomoura.ecommerceapi.bootstrap;

import com.brunomoura.ecommerceapi.config.AdminProperties;
import com.brunomoura.ecommerceapi.domain.user.User;
import com.brunomoura.ecommerceapi.enums.UserRole;
import com.brunomoura.ecommerceapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Profile("dev")
@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminProperties adminProperties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(AdminProperties adminProperties, UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminProperties = adminProperties;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        if (userRepository.existsByEmail(adminProperties.getUsername())) {
            logger.info("Admin already exists.");
            return;
        }

        User user = new User(
                "admin",
                adminProperties.getUsername(),
                "12345678909",
                "4199999-9999",
                LocalDate.of(2000,1,1),
                passwordEncoder.encode(adminProperties.getPassword())
        );

        user.updateRole(UserRole.ADMIN);

        userRepository.save(user);

        logger.info("Admin created successfully.");
    }
}
