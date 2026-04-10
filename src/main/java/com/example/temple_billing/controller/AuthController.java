package com.example.temple_billing.controller;

import com.example.temple_billing.config.JwtService;
import com.example.temple_billing.dto.LoginRequest;
import com.example.temple_billing.dto.LoginResponse;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        logger.info("Login attempt for username: {}", request.getUsername());

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getUsername(),
                                    request.getPassword()));

            User user = userRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow();

            String token = jwtService.generateToken(
                    user.getId(),
                    user.getUsername(),
                    user.getRole().name(),
                    user.getName()
            );

            logger.info("Login successful for username: {}", request.getUsername());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception ex) {
            logger.warn("Login failed for username: {} - Error: {}", request.getUsername(), ex.getMessage());
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid username or password"));

        }
    }
}