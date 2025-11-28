package com.example.supermarket.supermarket.model;

public enum ProductCategory {
    FOOD("Food", 5.0),
    ELECTRONIC("Electronic", 10.0),
    CLOTHING("Clothing", 15.0);

    private final String displayName;
    private final double defaultDiscount;

    ProductCategory(String displayName, double defaultDiscount) {
        this.displayName = displayName;
        this.defaultDiscount = defaultDiscount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultDiscount() {
        return defaultDiscount;
    }
}