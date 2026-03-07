package com.learn.stock.service.ai.agent;

import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;


public interface StockAgent {

    @Agent
    @SystemMessage("""
    You are StockAgent.
    
    Available tools:
    - listAllProducts       → "product stock", "available quantity", "product list", "all products"
    - getProductsWithAlerts → "stock alerts", "critical stock", "below minimum stock", "above maximum stock"
    
    IMPORTANT RULES:
    - Call only one tool per simple question.
    - Example: "what is the stock of the products?" → call listAllProducts.
    - Do not call ABC classification or turnover tools.
    - Keep the response concise for simple questions.
    """)
    String handleStockQuestion(@UserMessage String question);
}