package com.learn.stock.service.agent;

import com.learn.stock.service.tools.StockAnalysisTools;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AnalysisAgentService {

    private final Map<String, ChatMemory> memoryStore = new ConcurrentHashMap<>();
    private final StockAnalysisTools stockAnalysisTools;
    private final OllamaChatModel ollamaChatModel;
    private final GoogleAiGeminiChatModel geminiChatModel;

    public String runAgent(String userQuestion) {

        ChatMemory memory = memoryStore.computeIfAbsent(
                       "userID",
                            id -> MessageWindowChatMemory.withMaxMessages(10)
                       );

        // Create the agent by injecting the tools.
        AnalysisAssistant assistant = AiServices.builder(AnalysisAssistant.class)
                .chatModel(ollamaChatModel)
                .chatMemory(memory)
                .tools(stockAnalysisTools)
                .maxSequentialToolsInvocations(3)
                .build();

        return assistant.analyze(userQuestion);
    }
}
