package com.learn.stock.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProductDTO {
    private String name;
    private int minStockLevel;
    private int maxStockLevel;
    private int currentStock;
    private boolean obsolete;
}
