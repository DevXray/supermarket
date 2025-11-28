package com.example.supermarket.supermarket.repository;

import com.example.supermarket.supermarket.model.Supplier;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class SupplierRepository {
    private final Map<Long, Supplier> suppliers = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Supplier save(Supplier supplier) {
        if (supplier.getId() == null) {
            supplier.setId(idGenerator.getAndIncrement());
        }
        suppliers.put(supplier.getId(), supplier);
        return supplier;
    }

    public Optional<Supplier> findById(Long id) {
        return Optional.ofNullable(suppliers.get(id));
    }

    public Optional<Supplier> findByCode(String code) {
        return suppliers.values().stream()
                .filter(s -> s.getCode().equals(code))
                .findFirst();
    }

    public List<Supplier> findAll() {
        return new ArrayList<>(suppliers.values());
    }

    public void deleteById(Long id) {
        suppliers.remove(id);
    }

    public boolean existsByCode(String code) {
        return suppliers.values().stream()
                .anyMatch(s -> s.getCode().equals(code));
    }

    public long count() {
        return suppliers.size();
    }
}