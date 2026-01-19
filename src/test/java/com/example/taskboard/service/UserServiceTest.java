package com.example.taskboard.service;

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
        String email = "user123@example.test";
        String rawPassword = "password123";

        when(passwordEncoder.encode(rawPassword)).thenReturn("hashPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User newUser = userService.registerUser(email, rawPassword);

        assertEquals("hashPassword", newUser.getPasswordHash());
        assertEquals(email, newUser.getEmail());
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(newUser);
    }

    @Test
    void getUserByEmail_success() {
        String email = "user123@example.test";
        User testUser = new User(email, "placeholder");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        User responseUser = userService.getUserByEmail(email);

        assertEquals(email, responseUser.getEmail());
        verify(userRepository).findByEmail(email);
    }

    @Test
    void getUserByEmail_failure() {
        String email = "user123@example.test";

        when(userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserByEmail(email);
        });

        assertEquals("User not found.", exception.getMessage());
        verify(userRepository).findByEmail(email);
    }
}
