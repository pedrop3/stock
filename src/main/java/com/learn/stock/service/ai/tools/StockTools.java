package com.learn.stock.service.ai.tools;

import com.learn.stock.model.Product;
import com.learn.stock.service.ProductService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockTools {

    private final ProductService productService;

    @Tool("""
        Action: Lists the name and stock quantity of all products.
        When to use: questions about "product stock", "available quantity",
        "which products do we have", "product list", "show all products".
        DO NOT use for: ABC classification, turnover analysis, alerts or obsolete products.
        Returns: name and current stock level of each registered product.
    """)
    public String listAllProducts() {

        List<Product> products = productService.findAll(PageRequest.of(0, 100)).getContent();

        if (products.isEmpty()) {
            return "RESULT: No products registered in the system.";
        }

        String list = products.stream()
                .map(p -> String.format(
                        "- %s | stock: %d | min: %d | max: %d%s",
                        p.getName(),
                        p.getCurrentStock(),
                        p.getMinStockLevel(),
                        p.getMaxStockLevel(),
                        p.isObsolete() ? " | OBSOLETE" : ""
                ))
                .collect(Collectors.joining("\n"));

        return String.format("RESULT: %d product(s) found:%n%s", products.size(), list);
    }


    @Tool("""
        Action: Lists products with stock levels outside the defined limits (active alerts).
        When to use: questions about "stock alerts", "critical stock",
        "below minimum", "above maximum", "products with stock issues".
        DO NOT use for: listing all products or ABC classification.
        Returns: products with status BELOW_MINIMUM or ABOVE_MAXIMUM.
    """)
    public String getProductsWithAlerts() {

        List<Product> alerts = productService.findWithStockAlerts();

        if (alerts.isEmpty()) {
            return "RESULT: No products with stock alerts.";
        }

        String list = alerts.stream()
                .map(p -> {

                    boolean below = p.getCurrentStock() < p.getMinStockLevel();

                    return String.format(
                            "  - %s | %s | current: %d | limit: %d",
                            p.getName(),
                            below ? "BELOW MINIMUM" : "ABOVE MAXIMUM",
                            p.getCurrentStock(),
                            below ? p.getMinStockLevel() : p.getMaxStockLevel()
                    );
                })
                .collect(Collectors.joining("\n"));

        return String.format("RESULT: %d product(s) with alerts: %s", alerts.size(), list);
    }
}
