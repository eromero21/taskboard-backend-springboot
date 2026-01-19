package com.example.taskboard.service;

import com.example.taskboard.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public User authentication(String email, String rawPass) {
        User user = userService.getUserByEmail(email);

        if (!passwordEncoder.matches(rawPass, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid login info..");
        }

        return user;
    }
}
