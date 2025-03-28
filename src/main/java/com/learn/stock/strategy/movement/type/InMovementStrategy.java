package com.learn.stock.strategy.movement.type;

import com.learn.stock.model.Product;
import com.learn.stock.strategy.MovementTypeStrategy;
import org.springframework.stereotype.Component;

@Component
public class InMovementStrategy implements MovementTypeStrategy {

    @Override
    public void updateStock(Product product, int quantity) {
        product.setCurrentStock(product.getCurrentStock()  + quantity);
    }
}
