package com.learn.stock.service.ai.tools;

import com.learn.stock.model.Product;
import com.learn.stock.service.StockService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TurnoverTools {

    private final StockService stockService;

    @Tool(value = """
    Action: Returns the ABC classification grouping products into categories A, B and C.
    When to use: questions about "ABC classification", "category A", "category B",
    "category C", "ABC curve", "product priority".
    DO NOT use for: simple stock listing, alerts or turnover analysis.
    Returns: products grouped by category based on outbound movement volume.
    IMPORTANT: To filter multiple categories, pass them comma-separated: "B,C" or "A,B".
    Use "ALL" only when all categories are needed. Never call this tool multiple times
    for the same question.
    """)
    public String getAbcClassification(
            @P("Category filter: A, B, C, comma-separated like B,C, or ALL") String category) {

        Map<String, List<Product>> abc = stockService.classifyABC();

        Set<String> requestedCategories = Arrays.stream(category.split("[,;\\s]+"))
                .map(String::trim)
                .map(String::toUpperCase)
                .filter(s -> Set.of("A", "B", "C").contains(s))
                .collect(Collectors.toSet());

        // Fallback: ALL or invalid input → return everything
        if (requestedCategories.isEmpty() || category.equalsIgnoreCase("ALL")) {
            requestedCategories = Set.of("A", "B", "C");
        }

        final Set<String> finalCategories = requestedCategories;

        abc = abc.entrySet().stream()
                .filter(e -> finalCategories.contains(e.getKey().toUpperCase()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (abc.isEmpty()) {
            return "RESULT: No products found for the requested categories.";
        }

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
            Returns: top N products ordered by number of outbound movements.
            Default N is 10 if not specified by the user.
            """)
    public String getTurnoverSummary(@P("Number of top products to return: 5, 10 or 20") int limit) {

        int safeLimit = (limit == 5 || limit == 10 || limit == 20) ? limit : 10;

        Map<Product, Long> turnover = stockService.calculateTurnover();

        if (turnover.isEmpty()) {
            return "RESULT: No outbound movements recorded.";
        }

        String result = turnover.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(safeLimit)
                .map(e -> String.format(
                        "  - %-25s -> %d outbound movements",
                        e.getKey().getName(),
                        e.getValue()
                ))
                .collect(Collectors.joining("\n"));

        return "RESULT: Products ranked by outbound turnover:\n" + result;
    }
}
