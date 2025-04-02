package com.learn.stock.service;

import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;

import java.util.Map;

public interface StockMovementService {
    Map<Product, Long> findTurnoverGrouped(MovementType movementType);

    void save(StockMovement movement);
}
