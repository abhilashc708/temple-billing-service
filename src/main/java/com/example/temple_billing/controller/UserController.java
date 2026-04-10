package com.example.temple_billing.controller;

import com.example.temple_billing.dto.ChangePasswordRequest;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.UserRepository;
import com.example.temple_billing.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        logger.debug("Fetching users - Page: {}, Size: {}", page, size);
        return userService.getUsers(page, size);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User createUser(@RequestBody @Valid User user) {
        logger.info("Creating new user: {}", user.getUsername());
        User createdUser = userService.createUser(user);
        logger.info("User created successfully: {}", user.getUsername());
        return createdUser;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody @Valid User user) {
        logger.info("Updating user ID: {}", id);
        User updatedUser = userService.updateUser(id, user);
        logger.info("User ID: {} updated successfully", id);
        return updatedUser;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {
        logger.info("Delete request for user ID: {} by: {}", id, auth.getName());

        User user = userRepository.findById(id).orElseThrow();

        if (user.getUsername().equals(auth.getName())) {
            logger.warn("User {} attempted to delete their own account", auth.getName());
            return ResponseEntity.badRequest().body("You cannot delete your own account");
        }

        userService.deleteUser(id);
        logger.info("User ID: {} deleted successfully by: {}", id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        logger.debug("Fetching profile for user: {}", authentication.getName());
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            Authentication authentication,
            @RequestBody @Valid User updatedUser) {
        logger.info("User {} updating their profile", authentication.getName());

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());
        user.setDistrict(updatedUser.getDistrict());
        user.setState(updatedUser.getState());

        userRepository.save(user);
        logger.info("User {} profile updated successfully", username);

        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Authentication authentication) {
        logger.info("Password change request from user: {}", authentication.getName());

        String username = authentication.getName();

        userService.changePassword(username, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");
        logger.info("Password changed successfully for user: {}", username);

        return ResponseEntity.ok(response);
    }
}
