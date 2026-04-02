package com.example.taskboard.service;

import com.example.taskboard.exception.DuplicateEmailException;
import com.example.taskboard.model.User;
import com.example.taskboard.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void registerUser_success() {
        String email = "  User123@Example.Test ";
        String rawPassword = "password123";

        when(passwordEncoder.encode(rawPassword)).thenReturn("hashPassword");
        when(userRepository.existsByEmailIgnoreCase("user123@example.test")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User newUser = userService.registerUser(email, rawPassword);

        assertEquals("hashPassword", newUser.getPasswordHash());
        assertEquals("user123@example.test", newUser.getEmail());
        verify(userRepository).existsByEmailIgnoreCase("user123@example.test");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(newUser);
    }

    @Test
    void registerUser_failure_duplicateEmail() {
        String email = "user123@example.test";

        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

        DuplicateEmailException exception = assertThrows(DuplicateEmailException.class, () -> {
            userService.registerUser(email, "password123");
        });

        assertEquals("Email is already registered.", exception.getMessage());
        verify(userRepository).existsByEmailIgnoreCase(email);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByEmail_success() {
        String email = "user123@example.test";
        User testUser = new User(email, "placeholder");

        when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(testUser));

        User responseUser = userService.getUserByEmail(email);

        assertEquals(email, responseUser.getEmail());
        verify(userRepository).findByEmailIgnoreCase(email);
    }

    @Test
    void getUserByEmail_failure() {
        String email = "user123@example.test";

        when(userRepository.findByEmailIgnoreCase(any(String.class))).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserByEmail(email);
        });

        assertEquals("User not found with given email.", exception.getMessage());
        verify(userRepository).findByEmailIgnoreCase(email);
    }
}
