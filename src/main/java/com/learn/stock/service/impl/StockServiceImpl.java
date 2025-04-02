package com.learn.stock.service.impl;

import com.learn.stock.factory.MovementStrategyFactory;
import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;
import com.learn.stock.response.StockMovementRequest;
import com.learn.stock.service.ProductService;
import com.learn.stock.service.StockMovementService;
import com.learn.stock.service.StockService;
import com.learn.stock.strategy.MovementTypeStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductService productService;
    private final StockMovementService stockMovementService;
    private final MovementStrategyFactory strategyFactory;


    @Transactional
    public void recordMovement(StockMovementRequest stockMovementRequest) {
        log.info("Recording stock movement: {}", stockMovementRequest);

        Product product = productService.findByIdWithLock(stockMovementRequest.productId());

        if (product.getCurrentStock() < stockMovementRequest.quantity()) {
            throw new IllegalArgumentException("Insufficient stock for this operation.");
        }

        StockMovement movement = getStockMovement(stockMovementRequest, product);

        stockMovementService.save(movement);

        MovementTypeStrategy strategy = strategyFactory.getStrategy(stockMovementRequest.type());

        if (strategy != null) {
            strategy.updateStock(product, stockMovementRequest.quantity());
        }

        productService.save(product);
        log.info("Stock movement recorded successfully for product: {}", product.getId());
    }


    public List<Product> checkStockAlerts() {
        return productService.findWithStockAlerts();
    }

    public List<Product> getObsoleteProducts() {
        return productService.findAll()
                .stream()
                .filter(Product::isObsolete)
                .toList();
    }

    public Map<Product, Long> calculateTurnover() {
        return stockMovementService.findTurnoverGrouped(MovementType.OUT);
    }

    public Map<String, List<Product>> classifyABC() {
        var turnover = calculateTurnover();
        var sorted = turnover.entrySet()
                .stream()
                .sorted((a, b) ->
                        Long.compare(b.getValue(), a.getValue())
                ).toList();

        int total = sorted.size();
        int aEnd = (int) (total * 0.2);
        int bEnd = (int) (total * 0.5);

        Map<String, List<Product>> abcMap = new HashMap<>();
        abcMap.put("A", sorted.subList(0, aEnd).stream().map(Map.Entry::getKey).toList());
        abcMap.put("B", sorted.subList(aEnd, bEnd).stream().map(Map.Entry::getKey).toList());
        abcMap.put("C", sorted.subList(bEnd, total).stream().map(Map.Entry::getKey).toList());

        return abcMap;
    }

    private StockMovement getStockMovement(StockMovementRequest stockMovementRequest, Product product) {
        StockMovement movement = StockMovement.builder()
                .product(product)
                .quantity(stockMovementRequest.quantity())
                .dateTime(LocalDateTime.now())
                .type(stockMovementRequest.type())
                .build();

        return movement;
    }
}
