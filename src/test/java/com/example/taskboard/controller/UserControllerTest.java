package com.example.taskboard.controller;

import com.example.taskboard.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UserControllerTest {

    @Test
    void userInfo_success() {
        UserController userController = new UserController();

        User testUser = new User("user@example.test", "hashPass");
        ReflectionTestUtils.setField(testUser, "id", 123L);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(testUser, null, List.of());

        ResponseEntity<UserController.UserInfoResponse> resp = userController.userInfo(auth);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(123L, resp.getBody().id());
        assertEquals("user@example.test", resp.getBody().email());
    }

    @Test
    void userInfo_failure_authNull() {
        UserController userController = new UserController();

        ResponseEntity<UserController.UserInfoResponse> resp = userController.userInfo(null);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertNull(resp.getBody());
    }

    @Test
    void userInfo_failure_invalidAuthType() {
        UserController userController = new UserController();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken("randomItem", null);

        ResponseEntity<UserController.UserInfoResponse> resp = userController.userInfo(auth);

        assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
        assertNull(resp.getBody());
    }
}
