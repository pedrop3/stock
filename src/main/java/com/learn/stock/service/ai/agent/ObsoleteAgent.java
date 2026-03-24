package com.learn.stock.service.ai.agent;


import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface ObsoleteAgent {

    @Agent
    @SystemMessage("""
    You are ObsoleteAgent.
    
    Available tools:
    - getObsoleteProducts → "obsolete products", "discontinued products", "inactive products"
    
    IMPORTANT RULES:
    - Call only one tool per simple question.
    - Do not call stock, ABC, or turnover tools.
    - Keep the response concise for simple questions.
    """)
    String handleObsoleteQuestion(@UserMessage String question);
}
