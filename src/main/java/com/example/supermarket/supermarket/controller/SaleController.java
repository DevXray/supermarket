package com.example.supermarket.supermarket.controller;

import com.example.supermarket.supermarket.model.Sale;
import com.example.supermarket.supermarket.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @PostMapping
    public ResponseEntity<?> createSale(@RequestBody Sale sale) {
        try {
            Sale createdSale = saleService.createSale(sale);
            return new ResponseEntity<>(createdSale, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<Sale>> getAllSales() {
        return ResponseEntity.ok(saleService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSaleById(@PathVariable Long id) {
        return saleService.getSaleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/transaction/{code}")
    public ResponseEntity<?> getSaleByTransactionCode(@PathVariable String code) {
        return saleService.getSaleByTransactionCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<Sale>> getSalesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(saleService.getSalesByDateRange(startDate, endDate));
    }

    @GetMapping("/total-amount")
    public ResponseEntity<Map<String, Double>> getTotalSalesAmount() {
        return ResponseEntity.ok(Map.of("totalAmount", saleService.getTotalSalesAmount()));
    }

    @GetMapping("/total-amount/date-range")
    public ResponseEntity<Map<String, Double>> getTotalSalesAmountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        double total = saleService.getTotalSalesAmountByDateRange(startDate, endDate);
        return ResponseEntity.ok(Map.of("totalAmount", total));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalSalesCount() {
        return ResponseEntity.ok(Map.of("totalSales", saleService.getTotalSalesCount()));
    }
}