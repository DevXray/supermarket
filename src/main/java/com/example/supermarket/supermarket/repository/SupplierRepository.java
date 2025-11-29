package com.example.supermarket.supermarket.repository;

import com.example.supermarket.supermarket.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    Optional<Supplier> findByCode(String code);
    
    boolean existsByCode(String code);
}