package com.example.supermarket.supermarket.config;

import com.example.supermarket.supermarket.model.*;
import com.example.supermarket.supermarket.service.ProductService;
import com.example.supermarket.supermarket.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        // Create Suppliers
        Supplier supplier1 = new Supplier(null, "Fresh Foods Co.", "SUP001", 
                "John Doe", "081234567890", "freshfoods@example.com", 
                "Jl. Raya No. 123, Jakarta");
        supplier1 = supplierService.createSupplier(supplier1);

        Supplier supplier2 = new Supplier(null, "Electronics Distributor", "SUP002", 
                "Jane Smith", "082345678901", "electronics@example.com", 
                "Jl. Sudirman No. 456, Jakarta");
        supplier2 = supplierService.createSupplier(supplier2);

        Supplier supplier3 = new Supplier(null, "Fashion Wholesale", "SUP003", 
                "Bob Johnson", "083456789012", "fashion@example.com", 
                "Jl. Thamrin No. 789, Jakarta");
        supplier3 = supplierService.createSupplier(supplier3);

        // Create Food Products
        Product product1 = new Product(null, "Indomie Goreng", "FOOD001", 
                ProductCategory.FOOD, 3500, 150, 30, supplier1.getId());
        product1.setDescription("Mie instan rasa goreng");
        productService.createProduct(product1);

        Product product2 = new Product(null, "Beras Premium 5kg", "FOOD002", 
                ProductCategory.FOOD, 75000, 50, 10, supplier1.getId());
        product2.setDescription("Beras kualitas premium");
        productService.createProduct(product2);

        Product product3 = new Product(null, "Susu Ultra Milk 1L", "FOOD003", 
                ProductCategory.FOOD, 18000, 80, 20, supplier1.getId());
        product3.setDescription("Susu UHT full cream");
        productService.createProduct(product3);

        Product product4 = new Product(null, "Telur Ayam 1kg", "FOOD004", 
                ProductCategory.FOOD, 28000, 25, 15, supplier1.getId());
        product4.setDescription("Telur ayam segar");
        productService.createProduct(product4);

        // Create Electronic Products
        Product product5 = new Product(null, "Samsung Galaxy A54", "ELEC001", 
                ProductCategory.ELECTRONIC, 5500000, 15, 5, supplier2.getId());
        product5.setDescription("Smartphone Android terbaru");
        productService.createProduct(product5);

        Product product6 = new Product(null, "Laptop ASUS VivoBook", "ELEC002", 
                ProductCategory.ELECTRONIC, 8500000, 8, 3, supplier2.getId());
        product6.setDescription("Laptop untuk pekerjaan dan gaming ringan");
        productService.createProduct(product6);

        Product product7 = new Product(null, "Kipas Angin Miyako", "ELEC003", 
                ProductCategory.ELECTRONIC, 350000, 20, 5, supplier2.getId());
        product7.setDescription("Kipas angin berdiri 16 inch");
        productService.createProduct(product7);

        Product product8 = new Product(null, "Rice Cooker Cosmos", "ELEC004", 
                ProductCategory.ELECTRONIC, 450000, 12, 4, supplier2.getId());
        product8.setDescription("Rice cooker 1.8L");
        productService.createProduct(product8);

        // Create Clothing Products
        Product product9 = new Product(null, "Kaos Polos Cotton", "CLOTH001", 
                ProductCategory.CLOTHING, 75000, 100, 20, supplier3.getId());
        product9.setDescription("Kaos polos katun combed 30s");
        productService.createProduct(product9);

        Product product10 = new Product(null, "Celana Jeans Pria", "CLOTH002", 
                ProductCategory.CLOTHING, 250000, 40, 10, supplier3.getId());
        product10.setDescription("Celana jeans slim fit");
        productService.createProduct(product10);

        Product product11 = new Product(null, "Jaket Hoodie", "CLOTH003", 
                ProductCategory.CLOTHING, 180000, 35, 10, supplier3.getId());
        product11.setDescription("Jaket hoodie fleece");
        productService.createProduct(product11);

        Product product12 = new Product(null, "Sepatu Sneakers", "CLOTH004", 
                ProductCategory.CLOTHING, 320000, 18, 8, supplier3.getId());
        product12.setDescription("Sepatu sneakers casual");
        productService.createProduct(product12);

        System.out.println("===================================");
        System.out.println("Sample data has been initialized!");
        System.out.println("Total Suppliers: " + supplierService.getTotalSuppliers());
        System.out.println("Total Products: " + productService.getTotalProducts());
        System.out.println("===================================");
    }
}