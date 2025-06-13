package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByMenuItemId(Long menuItemId);

    @Query("SELECT i FROM Inventory i WHERE i.currentStock <= i.minimumStock")
    List<Inventory> findLowStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.currentStock = 0")
    List<Inventory> findOutOfStockItems();

    @Query("SELECT i FROM Inventory i WHERE i.currentStock < i.minimumStock")
    List<Inventory> findItemsNeedingRestock();

    @Query("SELECT i FROM Inventory i ORDER BY i.currentStock ASC")
    List<Inventory> findAllOrderByStockAsc();

    boolean existsByMenuItemId(Long menuItemId);
} 