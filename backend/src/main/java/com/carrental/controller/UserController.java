package com.carrental.controller;

import com.carrental.dto.request.SignupRequest;
import com.carrental.dto.response.UserResponse;
import com.carrental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin only)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUser(authentication, #id)")
    @Operation(summary = "Get user by ID (Admin or self)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create admin user (Admin only)")
    public ResponseEntity<UserResponse> createAdmin(@Valid @RequestBody SignupRequest signupRequest) {
        UserResponse user = userService.createAdmin(signupRequest);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.isUser(authentication, #id)")
    @Operation(summary = "Update user (Admin or self)")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody SignupRequest signupRequest) {
        UserResponse updatedUser = userService.updateUser(id, signupRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user (Admin only)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
