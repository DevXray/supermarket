package com.example.supermarket.supermarket.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales")
public class Sale {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String transactionCode;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "sale_id")
    private List<SaleItem> items = new ArrayList<>();
    
    @Column(nullable = false)
    private double totalAmount;
    
    @Column(nullable = false)
    private double totalDiscount;
    
    @Column(nullable = false)
    private double finalAmount;
    
    @Column(nullable = false)
    private LocalDateTime saleDate;

    // Constructors
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

    @PrePersist
    protected void onCreate() {
        saleDate = LocalDateTime.now();
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