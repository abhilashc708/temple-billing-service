package com.example.temple_billing.service;

import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.UserRepository;
import com.example.temple_billing.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        logger.info("Loading user details for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        logger.debug("User details loaded successfully for: {}", username);
        return new CustomUserDetails(user);
    }
}