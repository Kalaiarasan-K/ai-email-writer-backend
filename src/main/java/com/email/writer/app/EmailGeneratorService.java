package com.email.writer.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailGeneratorService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openrouter.api.url}")
    private String apiUrl;

    @Value("${openrouter.api.key}")
    private String apiKey;

    public String generateEmailReply(EmailRequest emailRequest) {

        String prompt = buildPrompt(emailRequest);

        Map<String, Object> requestBody = Map.of(
                "model", "mistralai/mistral-7b-instruct",
                "messages", new Object[]{
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                }
        );

        String response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Write a professional email reply. Do not include subject.\n");

        if (emailRequest.getTone() != null && !emailRequest.getTone().isBlank()) {
            prompt.append("Tone: ").append(emailRequest.getTone()).append("\n");
        }

        prompt.append("Original email:\n")
                .append(emailRequest.getEmailContent());

        return prompt.toString();
    }
}
