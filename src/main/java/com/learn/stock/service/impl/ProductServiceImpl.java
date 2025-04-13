package com.learn.stock.service.impl;

import com.learn.stock.model.Product;
import com.learn.stock.repository.ProductRepository;
import com.learn.stock.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> findWithStockAlerts() {
        return productRepository.findWithStockAlerts();
    }

    @Override
    public Product findByIdWithLock(Long id) {
        return productRepository.findByIdWithLock(id).orElseThrow();
    }

    @Override
    public void save(Product product) {
        productRepository.save(product);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> findByObsolete(Pageable pageable) {
        return productRepository.findByObsoleteIsTrue(pageable);
    }
}
