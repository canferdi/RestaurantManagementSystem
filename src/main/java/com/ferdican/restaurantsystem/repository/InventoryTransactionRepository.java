package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByInventoryIdOrderByTransactionDateDesc(Long inventoryId);

    List<InventoryTransaction> findByOrderId(Long orderId);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionDate BETWEEN ?1 AND ?2 ORDER BY it.transactionDate DESC")
    List<InventoryTransaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.transactionType = ?1 ORDER BY it.transactionDate DESC")
    List<InventoryTransaction> findByTransactionType(InventoryTransaction.TransactionType transactionType);

    @Query("SELECT it FROM InventoryTransaction it ORDER BY it.transactionDate DESC")
    List<InventoryTransaction> findAllOrderByTransactionDateDesc();
} 