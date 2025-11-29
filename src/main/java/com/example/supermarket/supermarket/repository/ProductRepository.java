package com.example.supermarket.supermarket.repository;

import com.example.supermarket.supermarket.model.Product;
import com.example.supermarket.supermarket.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByCode(String code);
    
    List<Product> findByCategory(ProductCategory category);
    
    @Query("SELECT p FROM Product p WHERE p.stock <= p.minimumStock")
    List<Product> findLowStockProducts();
    
    List<Product> findBySupplierId(Long supplierId);
    
    boolean existsByCode(String code);

    Optional<Product> findTopByCategoryOrderByIdDesc(ProductCategory category);
}