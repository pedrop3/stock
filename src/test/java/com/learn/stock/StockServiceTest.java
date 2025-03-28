package com.learn.stock;


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

import java.time.LocalDateTime;
import java.util.*;

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
    private MovementTypeStrategy inStrategy;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stockService = new StockServiceImpl(productRepo, movementRepo, strategyFactory);
    }

    @Test
    void testRecordMovementIncreasesStock() {
        Product product = new Product(1L, "Blue Pen", 5, 10, 10, false);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(strategyFactory.getStrategy(MovementType.IN)).thenReturn(inStrategy);


        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            p.setCurrentStock(p.getCurrentStock() + quantity);
            return null;
        }).when(inStrategy).updateStock(any(Product.class), eq(5));

        // act
        stockService.recordMovement(new StockMovementRequest(1L, 5, MovementType.IN));

        // assert
        Assertions.assertEquals(15, product.getCurrentStock());
        verify(productRepo).save(product);
        verify(movementRepo).save(any(StockMovement.class));

        ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);

        verify(movementRepo).save(movementCaptor.capture());
        StockMovement savedMovement = movementCaptor.getValue();

        Assertions.assertEquals(1L, savedMovement.getProduct().getId());
        Assertions.assertEquals(5, savedMovement.getQuantity());
        Assertions.assertEquals(MovementType.IN, savedMovement.getType());
    }

    @Test
    void testRecordMovementDecreasesStock() {
        Product product = new Product(1L, "Blue Pen", 5, 10, 10, false);
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(strategyFactory.getStrategy(MovementType.OUT)).thenReturn(inStrategy);

        doAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            int quantity = invocation.getArgument(1);
            p.setCurrentStock(p.getCurrentStock() - quantity);
            return null;
        }).when(inStrategy).updateStock(any(Product.class), eq(3));

        stockService.recordMovement( new StockMovementRequest(1L, 3, MovementType.OUT));

        Assertions.assertEquals(7, product.getCurrentStock());
        verify(productRepo).save(product);
        verify(movementRepo).save(any(StockMovement.class));
    }

    @Test
    void testRecordMovementWithInvalidProduct() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());
        var stockMovementRequest = new StockMovementRequest(99L, 10, MovementType.IN);

        Assertions.assertThrows(NoSuchElementException.class, () ->
                stockService.recordMovement(stockMovementRequest));
    }

    @Test
    void testCheckStockAlerts() {
        Product low = new Product(1L, "Blue Pen", 10, 50, 5, false);
        Product high = new Product(2L, "Black Pencil", 10, 50, 60, false);
        Product ok = new Product(3L, "Notebook", 10, 50, 45, false);
        when(productRepo.findAll()).thenReturn(List.of(low, high, ok));

        List<Product> alerts = stockService.checkStockAlerts();

        Assertions.assertEquals(2, alerts.size());
    }

    @Test
    void testGetObsoleteProducts() {
        Product obs1 = new Product(1L, "Blue Pen", 10, 50, 5, true);
        Product obs2 = new Product(2L, "Black Pencil", 10, 50, 60, true);
        Product normal = new Product(3L, "Notebook",  10, 50, 45, false);

        when(productRepo.findAll()).thenReturn(List.of(obs1, obs2, normal));

        List<Product> obsolete = stockService.getObsoleteProducts();

        Assertions.assertEquals(2, obsolete.size());

    }

    @Test
    void testCalculateTurnoverCountsOnlyOut() {
        Product product = new Product(1L, "Blue Pen", 10, 20, 40, false);

        List<StockMovement> movements = List.of(
                new StockMovement(1L, 2, LocalDateTime.now(), MovementType.OUT, product),
                new StockMovement(2L, 5, LocalDateTime.now(), MovementType.IN, product),
                new StockMovement(3L, 3, LocalDateTime.now(), MovementType.OUT, product)
        );

        when(productRepo.findAll()).thenReturn(List.of(product));
        when(movementRepo.findByProduct(product)).thenReturn(movements);

        Map<Product, Long> turnover = stockService.calculateTurnover();

        Assertions.assertEquals(2L, turnover.get(product));
    }

    @Test
    void testABCClassification() {
        Product a = new Product(1L, "Produto A", 10, 2, 50, false);
        Product b = new Product(2L, "Produto B", 20, 2, 50, false);
        Product c = new Product(3L, "Produto C", 30, 2, 50, false);
        Product d = new Product(4L, "Produto D", 40, 2, 50, false);
        Product e = new Product(5L, "Produto E", 50, 2, 50, false);

        when(productRepo.findAll()).thenReturn(List.of(a, b, c, d, e));

        when(movementRepo.findByProduct(any())).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            long count = switch (p.getName()) {
                case "Produto A" -> 5;
                case "Produto B" -> 3;
                case "Produto C" -> 2;
                case "Produto D" -> 1;
                case "Produto E" -> 0;
                default -> 0;
            };
            return Collections.nCopies((int) count,
                    new StockMovement(1L,10, LocalDateTime.now(), MovementType.OUT,p));
        });

        Map<String, List<Product>> abc = stockService.classifyABC();

        Assertions.assertTrue(abc.get("A").contains(a));
        Assertions.assertTrue(abc.get("C").contains(e));
        Assertions.assertEquals(5, abc.values().stream().mapToInt(List::size).sum());
    }
}
