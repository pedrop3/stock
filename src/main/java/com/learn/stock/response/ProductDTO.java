package com.learn.stock.response;

public record ProductDTO( String name, int minStockLevel, int maxStockLevel, int currentStock,boolean obsolete) {}
