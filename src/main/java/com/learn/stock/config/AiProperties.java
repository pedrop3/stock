package com.learn.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "ai")
@Data
public class AiProperties {

    private Ollama ollama;
    private Gemini gemini;
    private Claude claude;


    @Data
    public static class Ollama {
        private String baseUrl;
        private String modelName;
        private int timeout;
    }

    @Data
    public static class Gemini {
        private String apiKey;
        private String modelName;
    }

    @Data
    public static class Claude {
        private String apiKey;
        private String modelName;
    }
}
