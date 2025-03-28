package com.learn.stock;

import com.learn.stock.factory.MovementStrategyFactory;
import com.learn.stock.model.MovementType;
import com.learn.stock.strategy.MovementTypeStrategy;
import com.learn.stock.strategy.movement.type.InMovementStrategy;
import com.learn.stock.strategy.movement.type.OutMovementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class MovementStrategyFactoryTest {

    private MovementStrategyFactory factory;

    @BeforeEach
    void setUp() {
        InMovementStrategy inStrategy = new InMovementStrategy();
        OutMovementStrategy outStrategy = new OutMovementStrategy();

        factory = new MovementStrategyFactory(List.of(inStrategy, outStrategy));
    }

    @Test
    void testInStrategyIsMappedCorrectly() {
        MovementTypeStrategy strategy = factory.getStrategy(MovementType.IN);
        assertInstanceOf(InMovementStrategy.class, strategy);
    }

    @Test
    void testReturnAlsoUsesInStrategy() {
        MovementTypeStrategy strategy = factory.getStrategy(MovementType.RETURN);
        assertInstanceOf(InMovementStrategy.class, strategy);
    }

    @Test
    void testOutStrategyIsMappedCorrectly() {
        MovementTypeStrategy strategy = factory.getStrategy(MovementType.OUT);
        assertInstanceOf(OutMovementStrategy.class, strategy);
    }

    @Test
    void testTransferAlsoUsesOutStrategy() {
        MovementTypeStrategy strategy = factory.getStrategy(MovementType.TRANSFER);
        assertInstanceOf(OutMovementStrategy.class, strategy);
    }
}

