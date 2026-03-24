package com.learn.stock.service.ai.agent;


import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface TurnoverAgent {

    @Agent
    @SystemMessage("""
    You are TurnoverAgent.
    
    Available tools:
    - getAbcClassification → "ABC classification", "ABC curve", "category A", "category B", "category C"
    - getTurnoverSummary   → "turnover", "best selling products", "most moved products", "outbound ranking"
    
    IMPORTANT RULES:
    - Call only one tool per simple question.
    - Do not use stock alerts or obsolete product tools.
    - Keep the response concise for simple questions.
    """)
    String handleTurnoverQuestion(@UserMessage String question);
}