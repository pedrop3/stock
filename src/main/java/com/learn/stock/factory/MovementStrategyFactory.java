package com.learn.stock.factory;


import com.learn.stock.model.MovementType;
import com.learn.stock.strategy.MovementTypeStrategy;
import com.learn.stock.strategy.movement.type.InMovementStrategy;
import com.learn.stock.strategy.movement.type.OutMovementStrategy;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class MovementStrategyFactory {

    private final Map<MovementType, MovementTypeStrategy> strategies = new EnumMap<>(MovementType.class);

    public MovementStrategyFactory(List<MovementTypeStrategy> strategyList) {
        strategyList.forEach(strategy -> {
            switch (strategy) {
                case InMovementStrategy in -> {
                    strategies.put(MovementType.IN, strategy);
                    strategies.put(MovementType.RETURN, strategy);
                }
                case OutMovementStrategy out -> {
                    strategies.put(MovementType.OUT, strategy);
                    strategies.put(MovementType.TRANSFER, strategy);
                }
                default -> throw new IllegalStateException("Unknown strategy: " + strategy.getClass());
            }
        });

    }
    public MovementTypeStrategy getStrategy(MovementType type) {
        return strategies.get(type);
    }
}
