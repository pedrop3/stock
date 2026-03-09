package com.learn.stock.config;

import com.learn.stock.service.ai.TokenUsageListener;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.googleai.GeminiThinkingConfig;
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
                .numCtx(32768)
                .repeatPenalty(1.2)
                .think(true)
                //.stop(endResponse)
                .listeners(List.of(new TokenUsageListener()))
                .build();
    }

    @Bean
    public OllamaChatModel ollamaChatModelWithOutThinking() {
        return OllamaChatModel.builder()
                .baseUrl(properties.getOllama().getBaseUrl())
                .modelName(properties.getOllama().getModelName())
                .timeout(Duration.ofSeconds(properties.getOllama().getTimeout()))
                .logRequests(true)
                .logResponses(true)
                .temperature(0.0)
                .numPredict(512)
                .numCtx(32768)
                .repeatPenalty(1.2)
                .think(false)
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
                .temperature(0.0)
                .topP(0.8)
                .maxOutputTokens(1024)
                .thinkingConfig(GeminiThinkingConfig.builder()
                        .includeThoughts(true)
                        .thinkingBudget(512)
                        .build())
                .build();
    }

    @Bean
    public AnthropicChatModel anthropicChatModel(){
        return AnthropicChatModel.builder()
                .apiKey(properties.getClaude().getApiKey())
                .modelName(properties.getClaude().getModelName())
                .logRequests(true)
                .logResponses(true)
                .temperature(0.0)
                .timeout(Duration.ofSeconds(60))
                .listeners(List.of(new TokenUsageListener()))
                .build();
    }


}