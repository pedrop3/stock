package com.learn.stock.strategy;

import com.learn.stock.model.Product;

public interface MovementTypeStrategy {
    void updateStock(Product product, int quantity);
}
