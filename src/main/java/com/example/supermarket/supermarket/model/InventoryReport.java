package com.example.supermarket.supermarket.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class InventoryReport {
    private LocalDateTime reportDate;
    private int totalProducts;
    private double totalInventoryValue;
    private List<Product> lowStockProducts;
    private Map<ProductCategory, Integer> productsByCategory;
    private Map<ProductCategory, Double> valueByCategory;
    private List<Product> topValueProducts;

    public InventoryReport() {
        this.reportDate = LocalDateTime.now();
    }

    // Getters and Setters
    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(int totalProducts) {
        this.totalProducts = totalProducts;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(double totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public List<Product> getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(List<Product> lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }

    public Map<ProductCategory, Integer> getProductsByCategory() {
        return productsByCategory;
    }

    public void setProductsByCategory(Map<ProductCategory, Integer> productsByCategory) {
        this.productsByCategory = productsByCategory;
    }

    public Map<ProductCategory, Double> getValueByCategory() {
        return valueByCategory;
    }

    public void setValueByCategory(Map<ProductCategory, Double> valueByCategory) {
        this.valueByCategory = valueByCategory;
    }

    public List<Product> getTopValueProducts() {
        return topValueProducts;
    }

    public void setTopValueProducts(List<Product> topValueProducts) {
        this.topValueProducts = topValueProducts;
    }
}