package com.example.taskboard.controller;

import com.example.taskboard.model.User;
import com.example.taskboard.service.AuthService;
import com.example.taskboard.service.JwtService;
import com.example.taskboard.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("http://localhost:5173")
public class AuthController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UserService userService, JwtService jwtService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * @api {post} /auth/register Register new user
     * @apiName RegisterUser
     * @apiGroup Authentication
     *
     * @apiSuccess (201 CREATED) {HttpStatus} Successful registry, only status code return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@Valid @RequestBody UserRequest req) {
        userService.registerUser(req.email(), req.password());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * @api {post} /auth/login Login validation
     * @apiName LoginValidation
     * @apiGroup Authentication
     *
     * @apiSuccess (200 OK) {HttpStatus} Successful login
     */
    @PostMapping("/login")
    public ResponseEntity<String> loginValidation(@Valid @RequestBody UserRequest req) {
        User user = authService.authentication(req.email(), req.password());
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(token);
    }

    public record UserRequest(
            @NotBlank @Email String email,
            @NotBlank String password) {};

    public record LoginResponse(String token) {};

}
