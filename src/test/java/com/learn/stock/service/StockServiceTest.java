package com.learn.stock.service;


import com.learn.stock.factory.MovementStrategyFactory;
import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;
import com.learn.stock.repository.ProductRepository;
import com.learn.stock.repository.StockMovementRepository;
import com.learn.stock.response.StockMovementRequest;
import com.learn.stock.service.impl.StockServiceImpl;
import com.learn.stock.strategy.MovementTypeStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockServiceTest {

    @InjectMocks
    private StockServiceImpl stockService;

    @Mock
    ProductRepository productRepo;

    @Mock
    StockMovementRepository movementRepo;

    @Mock
    MovementStrategyFactory strategyFactory;

    @Mock
    MovementTypeStrategy inStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stockService = new StockServiceImpl(productRepo, movementRepo, strategyFactory);
    }


    @Test
    void givenInMovement_whenRecordMovement_thenStockIncreasesAndMovementSaved() {
        Product product = createProduct(1L, "Blue Pen", 5, 10, 10, false);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(strategyFactory.getStrategy(MovementType.IN)).thenReturn(inStrategy);

        simulateStockIncrease();

        stockService.recordMovement(createMovementRequest(1L, 5, MovementType.IN));

        assertEquals(15, product.getCurrentStock());
        verify(productRepo).save(product);
        verify(movementRepo).save(any(StockMovement.class));

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);

        verify(movementRepo).save(movementCaptor.capture());
        StockMovement savedMovement = movementCaptor.getValue();

        assertEquals(1L, savedMovement.getProduct().getId());
        assertEquals(5, savedMovement.getQuantity());
        assertEquals(MovementType.IN, savedMovement.getType());
    }

    @Test
    void givenOutMovement_whenRecordMovement_thenStockDecreasesAndMovementSaved() {
        Product product = createProduct(1L, "Blue Pen", 5, 10, 10, false);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(strategyFactory.getStrategy(MovementType.OUT)).thenReturn(inStrategy);

        simulateStockDecrease();

        stockService.recordMovement(createMovementRequest(1L, 3, MovementType.OUT));

        assertEquals(7, product.getCurrentStock());
        verify(productRepo).save(product);
        verify(movementRepo).save(any(StockMovement.class));
    }

    @Test
    void givenExcessiveOutMovement_whenRecordMovement_thenThrowsIllegalArgumentException() {
        Product product = createProduct(1L, "Blue Pen", 5, 10, 10, false);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(strategyFactory.getStrategy(MovementType.OUT)).thenReturn(inStrategy);

        // Simula estratégia que lança exceção ao tentar tirar mais do que o estoque atual
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            if (quantity > p.getCurrentStock()) throw new IllegalArgumentException("Not enough stock");
            p.setCurrentStock(p.getCurrentStock() - quantity);
            return null;
        }).when(inStrategy).updateStock(any(Product.class), eq(11));


        var createMovementRequest = createMovementRequest(1L, 11, MovementType.OUT);

        assertThrows(IllegalArgumentException.class, () ->
                stockService.recordMovement(createMovementRequest)
        );
    }

    @Test
    void givenInvalidProductId_whenRecordMovement_thenThrowsNoSuchElementException() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());
        var stockMovementRequest = new StockMovementRequest(99L, 10, MovementType.IN);

        Assertions.assertThrows(NoSuchElementException.class, () ->
                stockService.recordMovement(stockMovementRequest));
    }

    @Test
    void givenProducts_whenCheckStockAlerts_thenReturnLowAndHighOnly() {
        Product low = createProduct(1L, "Low", 10, 50, 5, false);
        Product high = createProduct(2L, "High", 10, 50, 60, false);
        Product ok = createProduct(3L, "Ok", 10, 50, 30, false);

        when(productRepo.findAll()).thenReturn(List.of(low, high, ok));

        List<Product> alerts = stockService.checkStockAlerts();

        assertEquals(2, alerts.size());
        assertTrue(alerts.contains(low));
        assertTrue(alerts.contains(high));
    }


    @Test
    void givenProducts_whenGetObsolete_thenReturnOnlyObsoletes() {
        Product obs1 = createProduct(1L, "Obs1", 10, 50, 5, true);
        Product obs2 = createProduct(2L, "Obs2", 10, 50, 15, true);
        Product normal = createProduct(3L, "Normal", 10, 50, 25, false);

        when(productRepo.findAll()).thenReturn(List.of(obs1, obs2, normal));

        List<Product> obsolete = stockService.getObsoleteProducts();

        assertEquals(2, obsolete.size());
        assertTrue(obsolete.contains(obs1));
        assertTrue(obsolete.contains(obs2));
    }

    @Test
    void givenOutMovements_whenCalculateTurnover_thenReturnCorrectCounts() {
        Product p1 = createProduct(1L, "P1", 10, 20, 40, false);
        Product p2 = createProduct(2L, "P2", 10, 20, 40, false);

        when(movementRepo.findTurnoverGrouped(MovementType.OUT))
                .thenReturn(List.of(new Object[]{p1, 2L}, new Object[]{p2, 3L}));

        Map<Product, Long> turnover = stockService.calculateTurnover();

        assertEquals(2L, turnover.get(p1));
        assertEquals(3L, turnover.get(p2));
    }

    @Test
    void givenTurnoverData_whenClassifyABC_thenReturnsCorrectClassification() {
        Product a = createProduct(1L, "A", 10, 50, 30, false);
        Product b = createProduct(2L, "B", 10, 50, 30, false);
        Product c = createProduct(3L, "C", 10, 50, 30, false);
        Product d = createProduct(4L, "D", 10, 50, 30, false);
        Product e = createProduct(5L, "E", 10, 50, 30, false);

        simulateTurnover(a, b, c, d, e);

        Map<String, List<Product>> result = stockService.classifyABC();

        assertEquals(List.of(a), result.get("A"));
        assertEquals(List.of(b), result.get("B"));
        assertEquals(List.of(c, d, e), result.get("C"));
    }

    private Product createProduct(Long id, String name, int min, int max, int current, boolean obsolete) {
        return new Product(id, name, min, max, current, obsolete);
    }

    private StockMovementRequest createMovementRequest(Long productId, int quantity, MovementType type) {
        return new StockMovementRequest(productId, quantity, type);
    }

    private void simulateStockIncrease() {
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            p.setCurrentStock(p.getCurrentStock() + quantity);
            return null;
        }).when(inStrategy).updateStock(any(Product.class), anyInt());
    }

    private void simulateStockDecrease() {
        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            p.setCurrentStock(p.getCurrentStock() - quantity);
            return null;
        }).when(inStrategy).updateStock(any(Product.class), anyInt());
    }

    private void simulateTurnover(Product... products) {
        List<Object[]> mockData = new ArrayList<>();
        long qty = 50;
        for (Product p : products) {
            mockData.add(new Object[]{p, qty});
            qty -= 10;
        }
        when(movementRepo.findTurnoverGrouped(MovementType.OUT)).thenReturn(mockData);
    }

}
