package com.example.temple_billing.service;

import com.example.temple_billing.dto.ChangePasswordRequest;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Page<User> getUsers(int page, int size) {
        logger.debug("Fetching users - Page: {}, Size: {}", page, size);
        return userRepository.findAll(PageRequest.of(page, size));
    }

    public User createUser(User user) {
        logger.info("Creating new user: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(Long id, User user) {
        logger.info("Updating user with ID: {}", id);
        User existing = userRepository.findById(id).orElseThrow();

        existing.setUsername(user.getUsername());
        existing.setName(user.getName());
        existing.setAddress(user.getAddress());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setRole(user.getRole());

        User updatedUser = userRepository.save(existing);
        logger.info("User ID: {} updated successfully", id);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
        logger.info("User ID: {} deleted successfully", id);
    }

    public void changePassword(String username, ChangePasswordRequest request) {
        logger.info("Password change requested for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", username);
                    return new RuntimeException("User not found");
                });

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            logger.warn("Incorrect current password for user: {}", username);
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("Password changed successfully for user: {}", username);
    }
}
