package com.learn.stock.service.impl;

import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;
import com.learn.stock.repository.StockMovementRepository;
import com.learn.stock.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository stockMovementRepository;

    @Override
    public Map<Product, Long> findTurnoverGrouped(MovementType movementType) {
        return  stockMovementRepository.findTurnoverGrouped(MovementType.OUT)
                .stream()
                .collect(Collectors.toMap(
                                productLongEntry -> (Product) productLongEntry[0],
                                productLongEntry -> (Long) productLongEntry[1]
                        )
                );
    }

    @Override
    public void save(StockMovement movement) {
        stockMovementRepository.save(movement);
    }
}
