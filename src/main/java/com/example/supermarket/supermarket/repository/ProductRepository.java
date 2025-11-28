package com.example.supermarket.supermarket.repository;

import com.example.supermarket.supermarket.model.Product;
import com.example.supermarket.supermarket.model.ProductCategory;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(idGenerator.getAndIncrement());
        }
        products.put(product.getId(), product);
        return product;
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    public Optional<Product> findByCode(String code) {
        return products.values().stream()
                .filter(p -> p.getCode().equals(code))
                .findFirst();
    }

    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public List<Product> findByCategory(ProductCategory category) {
        return products.values().stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Product> findLowStockProducts() {
        return products.values().stream()
                .filter(Product::isLowStock)
                .collect(Collectors.toList());
    }

    public List<Product> findBySupplierId(Long supplierId) {
        return products.values().stream()
                .filter(p -> p.getSupplierId() != null && p.getSupplierId().equals(supplierId))
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        products.remove(id);
    }

    public boolean existsByCode(String code) {
        return products.values().stream()
                .anyMatch(p -> p.getCode().equals(code));
    }

    public long count() {
        return products.size();
    }
}