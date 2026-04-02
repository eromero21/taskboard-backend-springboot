package com.example.taskboard.service;

import com.example.taskboard.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserService userService;

    @InjectMocks
    AuthService authService;

    @Test
    void authentication_success() {
        String email = "user123@example.test";
        String rawPassword = "rawPass";
        String hashPassword = "hashedPass";
        User testUser = new User(email, hashPassword);

        when(userService.getUserByEmail(email)).thenReturn(testUser);
        when(passwordEncoder.matches(rawPassword, hashPassword)).thenReturn(true);

        User newUser = authService.authenticate(email, rawPassword);

        assertEquals(testUser, newUser);
        verify(passwordEncoder).matches(any(), any());
        verify(userService).getUserByEmail(email);
    }

    @Test
    void authentication_failure() {
        String email = "user123@example.test";
        String password = "password";
        User testUser = new User(email, password);

        when(userService.getUserByEmail(email)).thenReturn(testUser);
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(email, password);
        });
        assertEquals("Invalid email or password.", exception.getMessage());
        verify(passwordEncoder).matches(password, testUser.getPasswordHash());
    }

    @Test
    void authentication_failure_userNotFound() {
        String email = "user123@example.test";

        when(userService.getUserByEmail(email))
                .thenThrow(new UsernameNotFoundException("User not found with given email."));

        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(email, "password");
        });

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(userService).getUserByEmail(email);
    }
}
