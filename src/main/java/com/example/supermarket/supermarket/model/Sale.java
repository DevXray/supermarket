package com.example.supermarket.supermarket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private Long id;
    private String transactionCode;
    private List<SaleItem> items;
    private double totalAmount;
    private double totalDiscount;
    private double finalAmount;
    private LocalDateTime saleDate;

    public Sale() {
        this.items = new ArrayList<>();
        this.saleDate = LocalDateTime.now();
    }

    public Sale(Long id, String transactionCode) {
        this.id = id;
        this.transactionCode = transactionCode;
        this.items = new ArrayList<>();
        this.saleDate = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items;
    }

    public void addItem(SaleItem item) {
        this.items.add(item);
        calculateTotals();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public void calculateTotals() {
        this.totalAmount = items.stream()
                .mapToDouble(SaleItem::getSubtotal)
                .sum();
        this.totalDiscount = items.stream()
                .mapToDouble(SaleItem::getDiscountAmount)
                .sum();
        this.finalAmount = totalAmount - totalDiscount;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", transactionCode='" + transactionCode + '\'' +
                ", finalAmount=" + finalAmount +
                ", saleDate=" + saleDate +
                '}';
    }
}