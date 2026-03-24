package com.learn.stock.service.ai.tools;

import com.learn.stock.service.ai.MetricsCollector;
import com.learn.stock.service.ai.agent.ObsoleteAgent;
import com.learn.stock.service.ai.agent.StockAgent;
import com.learn.stock.service.ai.agent.TurnoverAgent;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubAgentTools {

    private  StockAgent stockAgent;
    private  TurnoverAgent turnoverAgent;
    private  ObsoleteAgent obsoleteAgent;

    public void setAgents(StockAgent stockAgent,
                          TurnoverAgent turnoverAgent,
                          ObsoleteAgent obsoleteAgent) {
        this.stockAgent = stockAgent;
        this.turnoverAgent = turnoverAgent;
        this.obsoleteAgent = obsoleteAgent;
    }

    @Tool("Delegates stock and alert questions to StockAgent")
    public String askStockAgent(String question) {
        record("askStockAgent");
        return stockAgent.handleStockQuestion(question);
    }

    @Tool("Delegates ABC classification and turnover questions to TurnoverAgent")
    public String askTurnoverAgent(String question) {
        record("askTurnoverAgent");
        return turnoverAgent.handleTurnoverQuestion(question);
    }

    @Tool("Delegates obsolete/discontinued product questions to ObsoleteAgent")
    public String askObsoleteAgent(String question) {
        record("askObsoleteAgent");
        return obsoleteAgent.handleObsoleteQuestion(question);
    }

    private void record(String toolName) {
        MetricsCollector collector = MetricsCollector.current();
        if (collector != null) collector.recordTool(toolName);
    }
}