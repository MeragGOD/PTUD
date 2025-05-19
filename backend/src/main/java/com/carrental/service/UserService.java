package com.carrental.service;

import com.carrental.dto.request.PasswordResetRequest;
import com.carrental.dto.request.PasswordUpdateRequest;
import com.carrental.dto.request.SignupRequest;
import com.carrental.dto.response.UserResponse;
import com.carrental.exception.ResourceNotFoundException;
import com.carrental.model.Role;
import com.carrental.model.User;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public UserResponse createUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.CUSTOMER)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse createAdmin(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, SignupRequest signupRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setName(signupRequest.getName());
        if (!user.getEmail().equals(signupRequest.getEmail()) && userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already in use!");
        }
        user.setEmail(signupRequest.getEmail());
        if (signupRequest.getPassword() != null && !signupRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void initiatePasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Transactional
    public void resetPassword(PasswordUpdateRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setResetToken(null);
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
