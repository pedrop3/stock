package com.learn.stock.config;

import com.learn.stock.service.ai.TokenUsageListener;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AiModelConfig {

    private final AiProperties properties;
    private final List<String> endResponse = List.of("<|endoftext|>", "<|im_end|>", "\\n\\n\\n");

    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .modelName(properties.getOllama().getModelName())
                .timeout(Duration.ofSeconds(properties.getOllama().getTimeout()))
                .logRequests(true)
                .logResponses(true)
                .temperature(0.0)
                .numPredict(512)
                .numCtx(4096)
                .repeatPenalty(1.2)
                //.stop(endResponse)
                .listeners(List.of(new TokenUsageListener()))
                .build();
    }

    @Bean
    public GoogleAiGeminiChatModel googleModel() {
        return GoogleAiGeminiChatModel.builder()
                .apiKey(properties.getGemini().getApiKey())
                .modelName(properties.getGemini().getModelName())
                .logRequests(true)
                .logResponses(true)
                .logRequests(true)
                .logResponses(true)
                .temperature(0.0)
                .build();
    }
}