package com.learn.stock.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.stock.mapper.ProductMapper;
import com.learn.stock.model.MovementType;
import com.learn.stock.model.Product;
import com.learn.stock.response.ProductDTO;
import com.learn.stock.response.StockMovementRequest;
import com.learn.stock.response.TurnoverDTO;
import com.learn.stock.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("qa")
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    StockService stockService;

    @MockitoBean
    ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/stocks/movement should return 200 OK")
    void testRecordMovement() throws Exception {
        StockMovementRequest request = new StockMovementRequest(1L, 10, MovementType.IN);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/stocks/movement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/stocks/alerts should return product list")
    void getAlertsShouldReturnProductDTOList() throws Exception {

        List<Product> productList = List.of(
                new Product(1L,"Product 1", 5, 30, 0, false),
                new Product(2L,"Product 2", 5, 40, 1, false)
        );
        when(stockService.checkStockAlerts()).thenReturn(productList);

        // Mock do mapper convertendo produtos para DTOs
        List<ProductDTO> productDTOList = Arrays.asList(
                new ProductDTO("Product 1", 5, 30, 0, false),
                new ProductDTO("Product 2", 5, 40, 1, false)
        );
        when(productMapper.toDtoList(productList)).thenReturn(productDTOList);


        // Performing the GET request and asserting the response
        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/alerts"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(productDTOList)));

        verify(stockService, times(1)).checkStockAlerts();
        verify(productMapper, times(1)).toDtoList(productList);

    }


    @Test
    @DisplayName("GET /api/stocks/obsolete should return obsolete product list")
    void getObsoletesShouldReturnProductDTOList() throws Exception {

        List<Product> productList = List.of(
                new Product(1L,"Obsolete Product 1", 5, 30, 0, true),
                new Product(2L,"Obsolete Product 2", 5, 40, 1, true)
        );
        when(stockService.getObsoleteProducts()).thenReturn(productList);

        List<ProductDTO> productDTOList = Arrays.asList(
                new ProductDTO("Obsolete Product 1", 5, 30, 0, true),
                new ProductDTO("Obsolete Product 2", 5, 40, 1, true)
        );
        when(productMapper.toDtoList(productList)).thenReturn(productDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/obsolete"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(productDTOList)));

        verify(stockService, times(1)).getObsoleteProducts();
        verify(productMapper, times(1)).toDtoList(productList);
    }


    @Test
    @DisplayName("GET /api/stocks/turnover should return turnover DTO list")
    void testTurnover() throws Exception {
        Product product = new Product(1L, "Pen", 5, 50, 20, false);
        ProductDTO productDTO = new ProductDTO("Pen", 5, 50, 20, false);
        Long turnoverValue = 8L;

        when(stockService.calculateTurnover()).thenReturn(Map.of(product, turnoverValue));
        when(productMapper.toDto(product)).thenReturn(productDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/turnover"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].product.name").value("Pen"))
                .andExpect(jsonPath("$[0].product.minStockLevel").value(5))
                .andExpect(jsonPath("$[0].product.maxStockLevel").value(50))
                .andExpect(jsonPath("$[0].product.currentStock").value(20))
                .andExpect(jsonPath("$[0].product.obsolete").value(false))
                .andExpect(jsonPath("$[0].count").value(8));

        verify(stockService).calculateTurnover();
        verify(productMapper).toDto(product);
    }

    @Test
    @DisplayName("GET /api/stocks/abc should return ABC classification")
    void testABC() throws Exception {
        when(productMapper.toDtoMapList(any())).thenReturn(
                Map.of("A", List.of(), "B", List.of(), "C", List.of())
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.A").isArray());
    }
}