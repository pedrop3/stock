package com.learn.stock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.model.StockMovement;
import com.learn.stock.repository.ProductRepository;
import com.learn.stock.repository.StockMovementRepository;
import com.learn.stock.response.StockMovementRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("qa")
class StockControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/stocks";
        stockMovementRepository.deleteAll();
        productRepository.deleteAll();

        Product product = new Product();
        product.setName("Test Product");
        product.setMinStockLevel(10);
        product.setMaxStockLevel(100);
        product.setCurrentStock(20);
        product.setObsolete(false);
        productRepository.save(product);
    }

    @Test
    void testRecordMovementAndValidateStockUpdate() throws Exception {
        Product product = productRepository.findAll().getFirst();

        StockMovementRequest request = new StockMovementRequest(product.getId(), 15, MovementType.IN);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(request), headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/movement",
                HttpMethod.POST,
                entity,
                Void.class
        );

        assertEquals(200, response.getStatusCode().value());

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(35, updated.getCurrentStock());

        List<StockMovement> movements = stockMovementRepository.findByProduct(updated);
        assertEquals(1, movements.size());
        assertEquals(MovementType.IN, movements.getFirst().getType());
    }

    @Test
    void testRecordMovementOUT_negativeStock() throws Exception {
        Product product = productRepository.findAll().getFirst();

        StockMovementRequest request = new StockMovementRequest(product.getId(), 5, MovementType.OUT);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(request), headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                baseUrl + "/movement",
                HttpMethod.POST,
                entity,
                Void.class
        );

        assertEquals(200, response.getStatusCode().value());

        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(15, updated.getCurrentStock());
    }

}
