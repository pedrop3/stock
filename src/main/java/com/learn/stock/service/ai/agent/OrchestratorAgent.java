package com.learn.stock.service.ai.agent;


import com.learn.stock.response.AgentResponseDTO;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface OrchestratorAgent {


    @Agent
    @SystemMessage("""
    You are a general business assistant.

    - Route each user question to the correct subagent:
        StockAgent, TurnoverAgent, or ObsoleteAgent.
    - Do not answer domain-specific questions directly.
    - For simple questions, call the relevant subagent.
    - Keep responses concise when questions are simple.
    - Follow subagents’ instructions and tool usage rules.
    """)
    AgentResponseDTO analyze(@UserMessage String message);
}
