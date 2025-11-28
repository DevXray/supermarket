package com.example.supermarket.supermarket.service;

import com.example.supermarket.supermarket.model.Product;
import com.example.supermarket.supermarket.model.ProductCategory;
import com.example.supermarket.supermarket.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new RuntimeException("Product with code " + product.getCode() + " already exists");
        }
        
        // Set default discount based on category
        if (product.getDiscountPercentage() == 0) {
            product.setDiscountPercentage(product.getCategory().getDefaultDiscount());
        }
        
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        product.setName(productDetails.getName());
        product.setCategory(productDetails.getCategory());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setMinimumStock(productDetails.getMinimumStock());
        product.setDescription(productDetails.getDescription());
        product.setSupplierId(productDetails.getSupplierId());
        product.setDiscountPercentage(productDetails.getDiscountPercentage());

        return productRepository.save(product);
    }

    public void updateStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new RuntimeException("Insufficient stock. Available: " + product.getStock());
        }
        
        product.setStock(newStock);
        productRepository.save(product);
    }

    public void reduceStock(Long id, int quantity) {
        updateStock(id, -quantity);
    }

    public void addStock(Long id, int quantity) {
        updateStock(id, quantity);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductByCode(String code) {
        return productRepository.findByCode(code);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(ProductCategory category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> getProductsBySupplierId(Long supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        productRepository.deleteById(id);
    }

    public void applyCategoryDiscount(ProductCategory category, double discountPercentage) {
        List<Product> products = productRepository.findByCategory(category);
        products.forEach(product -> {
            product.setDiscountPercentage(discountPercentage);
            productRepository.save(product);
        });
    }

    public long getTotalProducts() {
        return productRepository.count();
    }
}