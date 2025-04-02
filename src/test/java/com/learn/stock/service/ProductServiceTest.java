package com.learn.stock.service;

import com.learn.stock.model.Product;
import com.learn.stock.repository.ProductRepository;
import com.learn.stock.service.impl.ProductServiceImpl;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    ProductServiceImpl productService;

    @Mock
    ProductRepository productRepository;


    @Test
    void giveAllProductsWithStockAlerts() {
        Product product = createProduct(1L, "Blue Pen", 5, 10, 10, false);

        when(productRepository.findWithStockAlerts()).thenReturn(List.of(product));

        var productList = productService.findWithStockAlerts();

        assertEquals(1L, productList.getFirst().getId());
        verify(productRepository).findWithStockAlerts();


    }

    @Test
    void givenProducts_whenCheckStockAlerts_thenReturnLowAndHighOnly() {
        Product low = createProduct(1L, "Low", 10, 50, 5, false);
        Product high = createProduct(2L, "High", 10, 50, 60, false);
        Product ok = createProduct(3L, "Ok", 10, 50, 30, false);

        when(productRepository.findWithStockAlerts()).thenReturn(List.of(low, high));

        List<Product> alerts = productService.findWithStockAlerts();

        assertEquals(2, alerts.size());
        assertTrue(alerts.contains(low));
        assertTrue(alerts.contains(high));
    }

    @Test
    void givenNoProductsWithAlerts_whenCheckStockAlerts_thenReturnEmptyList() {
        when(productRepository.findWithStockAlerts()).thenReturn(List.of());

        List<Product> alerts = productService.findWithStockAlerts();

        assertTrue(alerts.isEmpty());
    }


    private Product createProduct(Long id, String name, int min, int max, int current, boolean obsolete) {
        return new Product(id, name, min, max, current, obsolete);
    }

}
