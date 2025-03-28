package com.learn.stock.response;

import com.learn.stock.model.MovementType;

public record StockMovementRequest(Long productId, int quantity, MovementType type) {}