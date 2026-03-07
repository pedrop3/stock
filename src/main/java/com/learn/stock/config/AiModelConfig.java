package com.learn.stock.config;

import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class AiModelConfig {

    private final AiProperties properties;

    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .modelName(properties.getOllama().getModelName())
                .timeout(Duration.ofSeconds(properties.getOllama().getTimeout()))
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public GoogleAiGeminiChatModel googleModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(properties.getGemini().getApiKey())
                .modelName(properties.getGemini().getModelName())
                .build();
    }
}