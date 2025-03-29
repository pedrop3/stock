package com.learn.stock.service.impl;

import com.learn.stock.factory.MovementStrategyFactory;
import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;
import com.learn.stock.repository.ProductRepository;
import com.learn.stock.repository.StockMovementRepository;
import com.learn.stock.response.StockMovementRequest;
import com.learn.stock.service.StockService;
import com.learn.stock.strategy.MovementTypeStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepo;
    private final StockMovementRepository movementRepo;
    private final MovementStrategyFactory strategyFactory;

    @Transactional
    public void recordMovement(StockMovementRequest stockMovementRequest) {
        Product product = productRepo.findById(stockMovementRequest.productId()).orElseThrow();

        if (product.getCurrentStock() < stockMovementRequest.quantity()) {
            throw new IllegalArgumentException("Insufficient stock for this operation.");
        }

        StockMovement movement = StockMovement.builder()
                .product(product)
                .quantity(stockMovementRequest.quantity())
                .dateTime(LocalDateTime.now())
                .type(stockMovementRequest.type())
                .build();

        movementRepo.save(movement);

        MovementTypeStrategy strategy = strategyFactory.getStrategy(stockMovementRequest.type());

        if (strategy != null) {
            strategy.updateStock(product, stockMovementRequest.quantity());
        }

        productRepo.save(product);
    }

    public List<Product> checkStockAlerts() {
        return productRepo.findAll().stream().
                filter(p -> p.getCurrentStock() < p.getMinStockLevel() ||
                        p.getCurrentStock() > p.getMaxStockLevel())
                .toList();
    }

    public List<Product> getObsoleteProducts() {
        return productRepo.findAll().stream().filter(Product::isObsolete).toList();
    }

    public Map<Product, Long> calculateTurnover() {
        return movementRepo.findTurnoverGrouped(MovementType.OUT)
                .stream()
                .collect(Collectors.toMap(
                                productLongEntry -> (Product) productLongEntry[0],
                                productLongEntry -> (Long) productLongEntry[1]
                        )
                );

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
}
