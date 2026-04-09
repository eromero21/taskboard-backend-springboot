package com.example.taskboard.controller;

import com.example.taskboard.model.User;
import com.example.taskboard.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUser_success() {
        User savedUser = new User("user@example.test", "hashedPassword");
        ReflectionTestUtils.setField(savedUser, "id", 7L);

        AuthController.UserRequest request =
                new AuthController.UserRequest("user@example.test", "password123");

        when(authService.register(request.email(), request.password()))
                .thenReturn(new AuthService.AuthResult(savedUser, "jwt-token"));

        ResponseEntity<AuthController.AuthResponse> response = authController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(7L, response.getBody().id());
        assertEquals("user@example.test", response.getBody().email());
        assertEquals("jwt-token", response.getBody().token());
        verify(authService).register(request.email(), request.password());
    }

    @Test
    void loginValidation_success() {
        User user = new User("user@example.test", "hashedPassword");
        ReflectionTestUtils.setField(user, "id", 11L);

        AuthController.UserRequest request =
                new AuthController.UserRequest("user@example.test", "password123");

        when(authService.authenticate(request.email(), request.password())).thenReturn(user);
        when(authService.issueToken(user)).thenReturn("jwt-token");

        ResponseEntity<AuthController.AuthResponse> response = authController.loginValidation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(11L, response.getBody().id());
        assertEquals("user@example.test", response.getBody().email());
        assertEquals("jwt-token", response.getBody().token());
        verify(authService).authenticate(request.email(), request.password());
        verify(authService).issueToken(user);
    }
}
