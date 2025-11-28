package com.example.supermarket.supermarket.controller;

import com.example.supermarket.supermarket.model.InventoryReport;
import com.example.supermarket.supermarket.model.ProductCategory;
import com.example.supermarket.supermarket.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/report")
    public ResponseEntity<InventoryReport> getInventoryReport() {
        return ResponseEntity.ok(inventoryService.generateInventoryReport());
    }

    @GetMapping("/alerts")
    public ResponseEntity<Map<String, List<String>>> getLowStockAlerts() {
        return ResponseEntity.ok(Map.of("alerts", inventoryService.getLowStockAlerts()));
    }

    @GetMapping("/total-value")
    public ResponseEntity<Map<String, Double>> getTotalInventoryValue() {
        return ResponseEntity.ok(Map.of("totalValue", inventoryService.getTotalInventoryValue()));
    }

    @GetMapping("/value-by-category")
    public ResponseEntity<Map<ProductCategory, Double>> getInventoryValueByCategory() {
        return ResponseEntity.ok(inventoryService.getInventoryValueByCategory());
    }
}