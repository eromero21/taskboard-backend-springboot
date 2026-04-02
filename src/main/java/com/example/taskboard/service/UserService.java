package com.example.taskboard.service;

import com.example.taskboard.exception.DuplicateEmailException;
import com.example.taskboard.model.User;
import com.example.taskboard.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String rawPass) {
        String normalizedEmail = normalizeEmail(email);
        validatePassword(rawPass);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new DuplicateEmailException("Email is already registered.");
        }

        String hashPass = passwordEncoder.encode(rawPass);
        User newUser = new User(normalizedEmail, hashPass);

        return userRepository.save(newUser);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with given email."));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found with given id."));
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty.");
        }

        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validatePassword(String rawPass) {
        if (rawPass == null || rawPass.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (rawPass.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters.");
        }
    }
}
