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
public class ObsoleteTools {

    private final ProductService productService;
    
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
}
