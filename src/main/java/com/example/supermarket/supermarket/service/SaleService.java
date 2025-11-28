package com.example.supermarket.supermarket.service;

import com.example.supermarket.supermarket.model.Product;
import com.example.supermarket.supermarket.model.Sale;
import com.example.supermarket.supermarket.model.SaleItem;
import com.example.supermarket.supermarket.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductService productService;

    public Sale createSale(Sale sale) {
        // Generate transaction code
        String transactionCode = generateTransactionCode();
        sale.setTransactionCode(transactionCode);

        // Process each item and reduce stock
        for (SaleItem item : sale.getItems()) {
            Product product = productService.getProductById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));

            // Check stock availability
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName() + 
                                         ". Available: " + product.getStock() + 
                                         ", Requested: " + item.getQuantity());
            }

            // Set item details from product
            item.setProductName(product.getName());
            item.setProductCategory(product.getCategory());
            item.setUnitPrice(product.getPrice());
            item.setDiscountPercentage(product.getDiscountPercentage());

            // Reduce stock
            productService.reduceStock(product.getId(), item.getQuantity());
        }

        // Calculate totals
        sale.calculateTotals();

        return saleRepository.save(sale);
    }

    public Optional<Sale> getSaleById(Long id) {
        return saleRepository.findById(id);
    }

    public Optional<Sale> getSaleByTransactionCode(String code) {
        return saleRepository.findByTransactionCode(code);
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAll();
    }

    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findByDateRange(startDate, endDate);
    }

    public double getTotalSalesAmount() {
        return saleRepository.findAll().stream()
                .mapToDouble(Sale::getFinalAmount)
                .sum();
    }

    public double getTotalSalesAmountByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findByDateRange(startDate, endDate).stream()
                .mapToDouble(Sale::getFinalAmount)
                .sum();
    }

    public long getTotalSalesCount() {
        return saleRepository.count();
    }

    private String generateTransactionCode() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        return "TRX-" + timestamp;
    }
}