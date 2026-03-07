package com.learn.stock.service.tools;


import com.learn.stock.model.Product;
import com.learn.stock.service.ProductService;
import com.learn.stock.service.StockService;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class StockAnalysisTools {

    private final StockService stockService;
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

    @Tool(
    value = """
        Action: Returns the ABC classification grouping products into categories A, B and C.
        When to use: questions about "ABC classification", "category A", "category B",
        "category C", "ABC curve", "product priority".
        DO NOT use for: simple stock listing, alerts or turnover analysis.
        Returns: products grouped by category based on outbound movement volume.
        """
    )
    public String getAbcClassification() {

        Map<String, List<Product>> abc = stockService.classifyABC();

        StringBuilder sb = new StringBuilder("RESULT: Current ABC classification:\n");

        for (Map.Entry<String, List<Product>> entry : abc.entrySet()) {

            int count = entry.getValue().size();

            sb.append(String.format("%nCategory %s (%d product(s)):%n", entry.getKey(), count));

            if (entry.getValue().isEmpty()) {
                sb.append("  (no products)\n");
            } else {

                entry.getValue().forEach(p ->
                        sb.append(String.format(
                                "  - %s (stock: %d)%n",
                                p.getName(),
                                p.getCurrentStock()
                        ))
                );
            }
        }

        return sb.toString();
    }

    @Tool("""
            Action: Returns a ranking of products based on the number of outbound movements (turnover).
            When to use: questions about "stock turnover", "best selling products",
            "most moved products", "outbound ranking", "inventory turnover".
            DO NOT use for: checking current stock, ABC classification or alerts.
            Returns: top 10 products ordered by number of outbound movements.
            """)
    public String getTurnoverSummary() {

        Map<Product, Long> turnover = stockService.calculateTurnover();

        if (turnover.isEmpty()) {
            return "RESULT: No outbound movements recorded.";
        }

        String result = turnover.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(e -> String.format(
                        "  - %-25s -> %d outbound movements",
                        e.getKey().getName(),
                        e.getValue()
                ))
                .collect(Collectors.joining("\n"));

        return "RESULT: Products ranked by outbound turnover:\n" + result;
    }

    @Tool("""
            Action: Lists products marked with obsolete = true in the system.
            When to use: questions about "obsolete products", "discontinued items",
            "inactive products", "end of life", "products to discard".
            DO NOT use for: listing all products or checking stock alerts.
            Returns: name and stock quantity of obsolete products.
            """)
    public String getObsoleteProducts() {

        List<Product> obsoletes = productService
                .findByObsolete(PageRequest.of(0, 50)).getContent();

        if (obsoletes.isEmpty()) {
            return "RESULT: No products marked as obsolete.";
        }

        String list = obsoletes.stream()
                .map(p -> String.format(
                        "  - %s (stock: %d)",
                        p.getName(),
                        p.getCurrentStock()
                ))
                .collect(Collectors.joining("\n"));

        return String.format("RESULT: %d obsolete product(s): %s", obsoletes.size(), list);
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


