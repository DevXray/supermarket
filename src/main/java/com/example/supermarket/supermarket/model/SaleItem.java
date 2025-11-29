package com.example.supermarket.supermarket.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sale_items")
public class SaleItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private String productName;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory productCategory;
    
    @Column(nullable = false)
    private int quantity;
    
    @Column(nullable = false)
    private double unitPrice;
    
    @Column(nullable = false)
    private double discountPercentage;
    
    @Column(nullable = false)
    private double subtotal;
    
    @Column(nullable = false)
    private double discountAmount;
    
    @Column(nullable = false)
    private double totalPrice;

    // Constructors
    public SaleItem() {}

    public SaleItem(Long productId, String productName, ProductCategory productCategory,
                    int quantity, double unitPrice, double discountPercentage) {
        this.productId = productId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountPercentage = discountPercentage;
        calculatePrices();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculatePrices();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculatePrices();
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
        calculatePrices();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    private void calculatePrices() {
        this.subtotal = unitPrice * quantity;
        this.discountAmount = subtotal * discountPercentage / 100;
        this.totalPrice = subtotal - discountAmount;
    }

    @Override
    public String toString() {
        return "SaleItem{" +
                "productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                '}';
    }
}