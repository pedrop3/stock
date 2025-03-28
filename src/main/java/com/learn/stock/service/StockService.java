package com.learn.stock.service;

import com.learn.stock.model.Product;
import com.learn.stock.response.StockMovementRequest;

import java.util.List;
import java.util.Map;

public interface StockService {

    void recordMovement(StockMovementRequest stockMovementRequest);

    List<Product> checkStockAlerts();

    List<Product> getObsoleteProducts();

    Map<Product, Long> calculateTurnover();

    Map<String, List<Product>> classifyABC();
}
