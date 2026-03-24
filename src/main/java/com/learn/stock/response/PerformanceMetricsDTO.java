package com.learn.stock.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerformanceMetricsDTO {
    private long totalDurationMs;
    private long loadDurationMs;
    private long evalDurationMs;
}