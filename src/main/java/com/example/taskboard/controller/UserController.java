package com.example.taskboard.controller;

import com.example.taskboard.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@CrossOrigin("http://localhost:5173")
public class UserController {

    /**
     * @api {get} /user Register new user
     * @apiName UserInfo
     * @apiGroup User
     *
     * @apiSuccess (200 OK) {UserInfoResponse} Returns basic user information
     */
    @GetMapping
    public ResponseEntity<UserInfoResponse> userInfo(@AuthenticationPrincipal User user) {
        System.out.println("Principal class = " + user.getClass());
        System.out.println("Id = " + user.getId() + " email = " + user.getEmail());
        return new ResponseEntity<>(new UserInfoResponse(user.getId(), user.getEmail()), HttpStatus.OK);
    }

    public record UserInfoResponse(Long id, String email) {};
}
