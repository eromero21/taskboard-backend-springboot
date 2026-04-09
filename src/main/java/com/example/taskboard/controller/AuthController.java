package com.example.taskboard.controller;

import com.example.taskboard.model.User;
import com.example.taskboard.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:5173")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * @api {post} /auth/register Register new user
     * @apiName RegisterUser
     * @apiGroup Authentication
     *
     * @apiSuccess (201 CREATED) {AuthResponse} Successful registration with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody UserRequest req) {
        AuthService.AuthResult authResult = authService.register(req.email(), req.password());
        User user = authResult.user();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(user.getId(), user.getEmail(), authResult.token()));
    }

    /**
     * @api {post} /auth/login Login validation
     * @apiName LoginValidation
     * @apiGroup Authentication
     *
     * @apiSuccess (200 OK) {HttpStatus} Successful login
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginValidation(@Valid @RequestBody UserRequest req) {
        User user = authService.authenticate(req.email(), req.password());
        String token = authService.issueToken(user);

        return ResponseEntity.ok(new AuthResponse(user.getId(), user.getEmail(), token));
    }

    public record UserRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, max = 72) String password) {}

    public record AuthResponse(Long id, String email, String token) {}
}
