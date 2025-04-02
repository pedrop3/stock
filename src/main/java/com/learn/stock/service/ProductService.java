package com.learn.stock.service;

import com.learn.stock.model.Product;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductService {

    List<Product> findWithStockAlerts();

    Product findByIdWithLock(@Param("id") Long id);

    void save(Product product);

    List<Product> findAll();
}