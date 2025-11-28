package com.example.supermarket.supermarket.repository;

import com.example.supermarket.supermarket.model.Sale;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class SaleRepository {
    private final Map<Long, Sale> sales = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Sale save(Sale sale) {
        if (sale.getId() == null) {
            sale.setId(idGenerator.getAndIncrement());
        }
        sales.put(sale.getId(), sale);
        return sale;
    }

    public Optional<Sale> findById(Long id) {
        return Optional.ofNullable(sales.get(id));
    }

    public Optional<Sale> findByTransactionCode(String code) {
        return sales.values().stream()
                .filter(s -> s.getTransactionCode().equals(code))
                .findFirst();
    }

    public List<Sale> findAll() {
        return new ArrayList<>(sales.values());
    }

    public List<Sale> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return sales.values().stream()
                .filter(s -> !s.getSaleDate().isBefore(startDate) && !s.getSaleDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    public long count() {
        return sales.size();
    }
}