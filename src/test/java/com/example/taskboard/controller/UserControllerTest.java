//package com.example.taskboard.controller;
//
//import com.example.taskboard.model.User;
//import com.example.taskboard.security.JwtAuthFilter;
//import com.example.taskboard.security.PasswordSecurity;
//import com.example.taskboard.service.JwtService;
//import com.example.taskboard.service.UserService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
//import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//
//import java.util.List;
//
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(controllers = UserController.class)
//@TestPropertySource(properties = "app.seed-default-board=false")
//public class UserControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @MockitoBean
//    JwtAuthFilter jwtAuthFilter;
//
//    @MockitoBean
//    JwtService jwtService;
//
//    @MockitoBean
//    UserService userService;
//
//    @Test
//    void userInfo_success() throws Exception {
//        User testUser = new User("user@example.test", "hashPass");
//        ReflectionTestUtils.setField(testUser, "id", 123L);
//
//        System.out.println(testUser.getId());
//        System.out.println(testUser.getEmail());
//
//        UsernamePasswordAuthenticationToken auth =
//                new UsernamePasswordAuthenticationToken(testUser, null, List.of());
//
//        mockMvc.perform(get("/user").with(authentication(auth)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(123))
//                .andExpect(jsonPath("$.email").value("user@example.test"))
//                .andExpect(jsonPath("$.passwordHash").doesNotExist());
//    }
//
//}
