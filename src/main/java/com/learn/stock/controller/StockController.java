package com.learn.stock.controller;

import com.learn.stock.mapper.ProductMapper;
import com.learn.stock.response.ProductDTO;
import com.learn.stock.response.StockMovementRequest;
import com.learn.stock.response.TurnoverDTO;
import com.learn.stock.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final ProductMapper productMapper;

    @Operation(summary = "Register a stock movement")
    @PostMapping("/movement")
    public ResponseEntity<Void> recordMovement(@RequestBody StockMovementRequest request) {
        stockService.recordMovement(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get stock alerts for products")
    @GetMapping("/alerts")
    public ResponseEntity<List<ProductDTO>> getAlerts() {
        return ResponseEntity.ok(productMapper.toDtoList(stockService.checkStockAlerts()));
    }

    @Operation(summary = "Get obsolete or slow-moving products")
    @GetMapping("/obsolete")
    public ResponseEntity<Page<ProductDTO>> getObsoletes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(stockService.getObsoleteProducts(pageable).map(productMapper::toDto));
    }

    @Operation(summary = "Get turnover report for products")
    @GetMapping("/turnover")
    public ResponseEntity<List<TurnoverDTO>> turnover() {
        return ResponseEntity.ok(stockService.calculateTurnover().entrySet()
                .stream()
                .map(entry ->
                        new TurnoverDTO(
                                productMapper.toDto(entry.getKey()),
                                entry.getValue()
                        )
                )
                .toList());
    }

    @Operation(summary = "Get ABC classification of products")
    @GetMapping("/abc")
    public ResponseEntity<Map<String, List<ProductDTO>>> abc() {
        return ResponseEntity.ok(productMapper.toDtoMapList(stockService.classifyABC()));
    }
}
