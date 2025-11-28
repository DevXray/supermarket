package com.example.supermarket.supermarket.service;

import com.example.supermarket.supermarket.model.InventoryReport;
import com.example.supermarket.supermarket.model.Product;
import com.example.supermarket.supermarket.model.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private ProductService productService;

    public InventoryReport generateInventoryReport() {
        InventoryReport report = new InventoryReport();
        List<Product> allProducts = productService.getAllProducts();

        // Total products
        report.setTotalProducts(allProducts.size());

        // Total inventory value
        double totalValue = allProducts.stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
        report.setTotalInventoryValue(totalValue);

        // Low stock products
        report.setLowStockProducts(productService.getLowStockProducts());

        // Products by category
        Map<ProductCategory, Integer> productsByCategory = new HashMap<>();
        for (ProductCategory category : ProductCategory.values()) {
            int count = (int) allProducts.stream()
                    .filter(p -> p.getCategory() == category)
                    .count();
            productsByCategory.put(category, count);
        }
        report.setProductsByCategory(productsByCategory);

        // Value by category
        Map<ProductCategory, Double> valueByCategory = new HashMap<>();
        for (ProductCategory category : ProductCategory.values()) {
            double value = allProducts.stream()
                    .filter(p -> p.getCategory() == category)
                    .mapToDouble(p -> p.getPrice() * p.getStock())
                    .sum();
            valueByCategory.put(category, value);
        }
        report.setValueByCategory(valueByCategory);

        // Top 10 products by value
        List<Product> topProducts = allProducts.stream()
                .sorted((p1, p2) -> Double.compare(
                        p2.getPrice() * p2.getStock(),
                        p1.getPrice() * p1.getStock()
                ))
                .limit(10)
                .collect(Collectors.toList());
        report.setTopValueProducts(topProducts);

        return report;
    }

    public List<String> getLowStockAlerts() {
        List<Product> lowStockProducts = productService.getLowStockProducts();
        return lowStockProducts.stream()
                .map(p -> String.format("ALERT: Product '%s' (Code: %s) is low on stock! Current: %d, Minimum: %d",
                        p.getName(), p.getCode(), p.getStock(), p.getMinimumStock()))
                .collect(Collectors.toList());
    }

    public double getTotalInventoryValue() {
        return productService.getAllProducts().stream()
                .mapToDouble(p -> p.getPrice() * p.getStock())
                .sum();
    }

    public Map<ProductCategory, Double> getInventoryValueByCategory() {
        Map<ProductCategory, Double> valueByCategory = new HashMap<>();
        List<Product> allProducts = productService.getAllProducts();

        for (ProductCategory category : ProductCategory.values()) {
            double value = allProducts.stream()
                    .filter(p -> p.getCategory() == category)
                    .mapToDouble(p -> p.getPrice() * p.getStock())
                    .sum();
            valueByCategory.put(category, value);
        }

        return valueByCategory;
    }
}