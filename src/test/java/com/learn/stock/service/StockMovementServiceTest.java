package com.learn.stock.service;

import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.repository.StockMovementRepository;
import com.learn.stock.service.impl.StockMovementServiceImpl;
import com.learn.stock.service.impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StockMovementServiceTest {

    @Mock
    StockMovementRepository stockMovementRepository;

    @InjectMocks
    StockMovementServiceImpl stockMovementService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product2 = new Product();
        product2.setId(2L);
    }

    @Test
    void findTurnoverGrouped_shouldReturnCorrectMap() {
        // Arrange
        Object[] entry1 = {product1, 10L};
        Object[] entry2 = {product2, 20L};
        List<Object[]> turnoverList = List.of(entry1, entry2);

        when(stockMovementRepository.findTurnoverGrouped(MovementType.OUT)).thenReturn(turnoverList);

        // Act
        Map<Product, Long> result = stockMovementService.findTurnoverGrouped(MovementType.OUT);

        // Assert
        assertEquals(2, result.size());
        assertEquals(10L, result.get(product1));
        assertEquals(20L, result.get(product2));
    }

    @Test
    void findTurnoverGrouped_shouldReturnEmptyMapWhenNoData() {
        // Arrange
        when(stockMovementRepository.findTurnoverGrouped(MovementType.OUT)).thenReturn(List.of());

        // Act
        Map<Product, Long> result = stockMovementService.findTurnoverGrouped(MovementType.OUT);

        // Assert
        assertEquals(0, result.size());
    }

    private Product createProduct(Long id, String name, int min, int max, int current, boolean obsolete) {
        return new Product(id, name, min, max, current, obsolete);
    }
}
