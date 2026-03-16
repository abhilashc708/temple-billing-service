package com.example.temple_billing.controller;

import com.example.temple_billing.dto.ChangePasswordRequest;
import com.example.temple_billing.entity.User;
import com.example.temple_billing.repository.UserRepository;
import com.example.temple_billing.service.UserService;
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

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return userService.getUsers(page, size);

    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public User createUser(@RequestBody User user) {

        return userService.createUser(user);

    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {

        return userService.updateUser(id, user);

    }
//    @PreAuthorize("hasRole('ADMIN')")
//    @DeleteMapping("/{id}")
//    public void deleteUser(@PathVariable Long id){
//
//        userService.deleteUser(id);
//
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication auth) {

        User user = userRepository.findById(id).orElseThrow();

        if (user.getUsername().equals(auth.getName())) {
            return ResponseEntity.badRequest().body("You cannot delete your own account");
        }

        userService.deleteUser(id);

        //return ResponseEntity.ok("Deleted");
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {

        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            Authentication authentication,
            @RequestBody User updatedUser) {

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

        return ResponseEntity.ok(user);
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        String username = authentication.getName();

        userService.changePassword(username, request);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password updated successfully");

        return ResponseEntity.ok(response);
    }

}
