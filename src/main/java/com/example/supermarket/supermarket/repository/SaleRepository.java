package com.example.supermarket.supermarket.repository;

import com.example.supermarket.supermarket.model.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    Optional<Sale> findByTransactionCode(String code);
    
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}