package com.carrental.security;

import com.carrental.model.User;
import com.carrental.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final UserRepository userRepository;

    public boolean isUser(Authentication authentication, Long userId) {
        String email = authentication.getName();
        return userRepository.findById(userId)
                .map(user -> user.getEmail().equals(email))
                .orElse(false);
    }
}
