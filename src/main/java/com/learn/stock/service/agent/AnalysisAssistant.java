package com.learn.stock.service.agent;


import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface AnalysisAssistant {


    @Agent
    @SystemMessage("""
        Available tools and EXCLUSIVE keyword mapping:
    
        - listAllProducts       → "product stock", "available quantity", "product list", "all products"
        - getAbcClassification  → "ABC classification", "ABC curve", "category A", "category B", "category C"
        - getTurnoverSummary    → "turnover", "best selling products", "most moved products", "outbound ranking"
        - getObsoleteProducts   → "obsolete products", "discontinued products", "inactive products"
        - getProductsWithAlerts → "stock alerts", "critical stock", "below minimum stock", "above maximum stock"
    
        IMPORTANT RULES:
    
        - Call ONLY ONE tool for simple questions.
        - Example: "what is the stock of the products?" → call listAllProducts.
          Do NOT call ABC classification or stock alerts.
    
        - The response length should match the question complexity:
          simple question → return a direct list without analysis.
    """)
    String analyze(@UserMessage String message);
}
