package com.example.taskboard.service;

import com.example.taskboard.model.User;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResult register(String email, String rawPass) {
        User user = userService.registerUser(email, rawPass);
        return new AuthResult(user, issueToken(user));
    }

    public User authenticate(String email, String rawPass) {
        User user;
        try {
            user = userService.getUserByEmail(email);
        } catch (UsernameNotFoundException exception) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        if (!passwordEncoder.matches(rawPass, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password.");
        }

        return user;
    }

    public String issueToken(User user) {
        return jwtService.generateToken(user);
    }

    public record AuthResult(User user, String token) {}
}
