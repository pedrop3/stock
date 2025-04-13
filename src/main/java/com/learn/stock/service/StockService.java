package com.learn.stock.service;

import com.learn.stock.model.Product;
import com.learn.stock.response.StockMovementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface StockService {

    void recordMovement(StockMovementRequest stockMovementRequest);

    List<Product> checkStockAlerts();

    Page<Product> getObsoleteProducts(Pageable pageable);

    Map<Product, Long> calculateTurnover();

    Map<String, List<Product>> classifyABC();
}
