package com.example.taskboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.StructuredResponse;
import com.openai.models.responses.StructuredResponseCreateParams;

@Service
public class AiService {
    private final OpenAIClient client;

    public AiService(OpenAIClient client) {
        this.client = client;
    }

    public GeneratedTasksResponse generateTaskboardTasks(String projectIdea) {
        String prompt = """
        Generate 5 practical starter tasks for the following project idea.
        
        Project idea: %s

        Return valid JSON only in this exact shape:
        {
            "tasks": [
                {
                    "title": "...",
                    "description": "..."
                }
            ]
        }

        Rules: No markdown, no numbering, no explanation outside JSON, title under 10 words, description under 25 words
        """.formatted(projectIdea);

        StructuredResponseCreateParams<GeneratedTasksResponse> params =
            StructuredResponseCreateParams.<GeneratedTasksResponse>builder()
            .model(ChatModel.GPT_5_4_MINI_2026_03_17)
            .input(prompt)
            .text(GeneratedTasksResponse.class)
            .build();

        StructuredResponse<GeneratedTasksResponse> response = client.responses().create(params);

        return response.output().stream()
            .filter(item -> item.isMessage())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No message returned from OpenAI"))
            .asMessage()
            .content().stream()
            .filter(content -> content.isOutputText())
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No structured output returned from OpenAI"))
            .asOutputText();
    }

    public record GeneratedTasksResponse(List<TaskItem> tasks) {}

    public record TaskItem(String title, String description) {}
}
