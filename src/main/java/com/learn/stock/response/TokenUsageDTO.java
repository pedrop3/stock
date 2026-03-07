package com.learn.stock.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenUsageDTO {
    private int inputTokens;
    private int outputTokens;
    private int totalTokens;
}