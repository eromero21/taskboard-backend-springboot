package com.example.taskboard.controller;

import com.example.taskboard.model.User;
import com.example.taskboard.service.AiService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiController {
    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate-tasks")
    public AiService.GeneratedTasksResponse generateProjectTasks(Authentication authentication,
                                                                @RequestBody ProjectIdeaRequest request) {
        requireUser(authentication);
        return aiService.generateTaskboardTasks(request.projectIdea());
    }

    private User requireUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        return user;
    }

    public record ProjectIdeaRequest(String projectIdea) {}
}
