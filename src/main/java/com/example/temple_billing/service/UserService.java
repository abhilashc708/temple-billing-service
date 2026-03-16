package com.example.temple_billing.service;

import com.example.temple_billing.dto.ChangePasswordRequest;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Page<User> getUsers(int page, int size) {

        return userRepository.findAll(PageRequest.of(page, size));

    }

    public User createUser(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);

    }

    public User updateUser(Long id, User user) {

        User existing = userRepository.findById(id).orElseThrow();

        existing.setUsername(user.getUsername());
        existing.setName(user.getName());
        existing.setAddress(user.getAddress());
        existing.setEmail(user.getEmail());
        existing.setPhone(user.getPhone());
        existing.setRole(user.getRole());

        return userRepository.save(existing);

    }

    public void deleteUser(Long id) {

        userRepository.deleteById(id);

    }

    public void changePassword(String username, ChangePasswordRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);

    }
}
