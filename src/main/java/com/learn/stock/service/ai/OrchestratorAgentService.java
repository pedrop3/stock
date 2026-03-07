package com.learn.stock.service.ai;

import com.learn.stock.response.AgentResponseDTO;
import com.learn.stock.response.PerformanceMetricsDTO;
import com.learn.stock.response.TokenUsageDTO;
import com.learn.stock.service.ai.agent.*;
import com.learn.stock.service.ai.tools.ObsoleteTools;
import com.learn.stock.service.ai.tools.StockTools;
import com.learn.stock.service.ai.tools.SubAgentTools;
import com.learn.stock.service.ai.tools.TurnoverTools;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class OrchestratorAgentService {

    private final Map<String, ChatMemory> memoryStore = new ConcurrentHashMap<>();
    private final StockTools stockTools;
    private final TurnoverTools turnoverTools;
    private final ObsoleteTools obsoleteTools;
    private final SubAgentTools subAgentTools;
    private final OllamaChatModel ollamaChatModel;
    private final GoogleAiGeminiChatModel geminiChatModel;

    private OrchestratorAgent orchestratorAgent;

    @PostConstruct
    public void buildSubAgents() {


        // Each subagent gets ONLY its own ToolProvider
        StockAgent stockAgent = AiServices.builder(StockAgent.class)
                .chatModel(ollamaChatModel)
                .toolProvider(new AgentToolProvider(stockTools))   // ← ToolProvider here
                .maxSequentialToolsInvocations(2)
                .build();

        TurnoverAgent turnoverAgent = AiServices.builder(TurnoverAgent.class)
                .chatModel(ollamaChatModel)
                .toolProvider(new AgentToolProvider(turnoverTools)) // ← ToolProvider here
                .maxSequentialToolsInvocations(2)
                .build();

        ObsoleteAgent obsoleteAgent = AiServices.builder(ObsoleteAgent.class)
                .chatModel(ollamaChatModel)
                .toolProvider(new AgentToolProvider(obsoleteTools)) // ← ToolProvider here
                .maxSequentialToolsInvocations(2)
                .build();

        // Inject built subagents into the wrapper so the orchestrator can call them
        // (if SubAgentTools receives them via constructor/setter)
        subAgentTools.setAgents(stockAgent, turnoverAgent, obsoleteAgent);

        // Build orchestrator once (stateless — memory handled per request)
        orchestratorAgent = AiServices.builder(OrchestratorAgent.class)
                .chatModel(ollamaChatModel)
                .tools(subAgentTools)
                .maxSequentialToolsInvocations(10)
                .build();
    }

    public AgentResponseDTO runAgent(String userQuestion) {

        MetricsCollector collector = MetricsCollector.start();
        long startTime = System.currentTimeMillis();
        boolean isFallback = false;
        String answer = null;

        try {
            ChatMemory memory = memoryStore.computeIfAbsent(
                    "userID", id -> MessageWindowChatMemory.withMaxMessages(10));


            OrchestratorAgent orchestrator = AiServices.builder(OrchestratorAgent.class)
                    .chatModel(ollamaChatModel)
                    .chatMemory(memory)
                    .tools(subAgentTools)
                    .maxSequentialToolsInvocations(3)
                    .build();

            answer = orchestrator.analyze(userQuestion).getAnswer();

        } catch (Exception e) {
            isFallback = true;
        } finally {
            MetricsCollector.clear(); // always clean ThreadLocal
        }

        long executionTime = System.currentTimeMillis() - startTime;
        TokenUsage usage = collector.getTokenUsage();

        return AgentResponseDTO.builder()
                .answer(answer)
                .stepsTaken(collector.getSteps())
                .toolsUsed(collector.getToolsUsed())
                .isFallback(isFallback)
                .toolExecutionTimeMs(executionTime)
                .tokenUsage(usage != null ? TokenUsageDTO.builder()
                        .inputTokens(usage.inputTokenCount())
                        .outputTokens(usage.outputTokenCount())
                        .totalTokens(usage.totalTokenCount())
                        .build() : null)
                .performance(PerformanceMetricsDTO.builder()
                        .totalDurationMs(executionTime)
                        .build())
                .build();
    }
}
