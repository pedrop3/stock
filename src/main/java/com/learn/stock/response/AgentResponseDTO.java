package com.learn.stock.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AgentResponseDTO {
    private String answer;

    // Métricas técnicas
    private TokenUsageDTO tokenUsage;
    private PerformanceMetricsDTO performance;

    // Métricas de Orquestração (Crucial para subagentes)
    private int stepsTaken;             // Quantas chamadas foram necessárias
    private List<String> toolsUsed;     // Ex: ["getAbcClassification"]

    // Status de Qualidade
    private boolean isFallback;         // Se foi uma resposta genérica de erro
    private long toolExecutionTimeMs;   // Quanto tempo o Java demorou a processar
}
