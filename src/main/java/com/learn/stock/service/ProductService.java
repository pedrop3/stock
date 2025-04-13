package com.learn.stock.service;

import com.learn.stock.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface ProductService {

    List<Product> findWithStockAlerts();

    Product findByIdWithLock(@Param("id") Long id);

    void save(Product product);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByObsolete(Pageable pageable);
}