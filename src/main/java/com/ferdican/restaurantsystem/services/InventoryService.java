package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.*;
import com.ferdican.restaurantsystem.repository.InventoryRepository;
import com.ferdican.restaurantsystem.repository.InventoryTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryTransactionRepository transactionRepository;
    private final MenuActivityService menuActivityService;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, 
                          InventoryTransactionRepository transactionRepository,
                          MenuActivityService menuActivityService) {
        this.inventoryRepository = inventoryRepository;
        this.transactionRepository = transactionRepository;
        this.menuActivityService = menuActivityService;
    }

    // Create inventory for a menu item
    public Inventory createInventory(Long menuItemId, Integer currentStock, Integer minimumStock, Integer maximumStock) {
        MenuItem menuItem = menuActivityService.getMenuItemById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));

        if (inventoryRepository.existsByMenuItemId(menuItemId)) {
            throw new IllegalStateException("Inventory already exists for this menu item");
        }

        Inventory inventory = new Inventory();
        inventory.setMenuItem(menuItem);
        inventory.setCurrentStock(currentStock);
        inventory.setMinimumStock(minimumStock);
        inventory.setMaximumStock(maximumStock);

        return inventoryRepository.save(inventory);
    }

    // Get inventory by menu item ID
    public Optional<Inventory> getInventoryByMenuItemId(Long menuItemId) {
        return inventoryRepository.findByMenuItemId(menuItemId);
    }

    // Get all inventory items
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAllOrderByStockAsc();
    }

    // Get low stock items
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    // Get out of stock items
    public List<Inventory> getOutOfStockItems() {
        return inventoryRepository.findOutOfStockItems();
    }

    // Add stock to inventory
    public Inventory addStock(Long menuItemId, Integer quantity, Users user, String notes) {
        Inventory inventory = getInventoryByMenuItemId(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for menu item"));

        int previousStock = inventory.getCurrentStock();
        
        try {
            inventory.addStock(quantity);
            inventory = inventoryRepository.save(inventory);
            
            // Record transaction
            recordTransaction(inventory, InventoryTransaction.TransactionType.STOCK_ADDED, 
                            quantity, previousStock, inventory.getCurrentStock(), user, notes, null);
            
            return inventory;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Failed to add stock: " + e.getMessage());
        }
    }

    // Remove stock from inventory
    public Inventory removeStock(Long menuItemId, Integer quantity, Users user, String notes) {
        Inventory inventory = getInventoryByMenuItemId(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for menu item"));

        int previousStock = inventory.getCurrentStock();
        
        try {
            inventory.removeStock(quantity);
            inventory = inventoryRepository.save(inventory);
            
            // Record transaction
            recordTransaction(inventory, InventoryTransaction.TransactionType.STOCK_REMOVED, 
                            quantity, previousStock, inventory.getCurrentStock(), user, notes, null);
            
            return inventory;
        } catch (IllegalStateException e) {
            throw new IllegalStateException("Failed to remove stock: " + e.getMessage());
        }
    }

    // Reserve stock for order
    public void reserveStockForOrder(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Long menuItemId = orderItem.getMenuItem().getId();
            Integer quantity = orderItem.getQuantity();
            
            Inventory inventory = getInventoryByMenuItemId(menuItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found for menu item: " + orderItem.getMenuItem().getName()));

            if (!inventory.canReserve(quantity)) {
                throw new IllegalStateException("Insufficient stock for " + orderItem.getMenuItem().getName() + 
                                              ". Available: " + inventory.getCurrentStock() + ", Requested: " + quantity);
            }

            int previousStock = inventory.getCurrentStock();
            inventory.reserveStock(quantity);
            inventoryRepository.save(inventory);
            
            // Record transaction
            recordTransaction(inventory, InventoryTransaction.TransactionType.ORDER_RESERVED, 
                            quantity, previousStock, inventory.getCurrentStock(), null, 
                            "Order #" + order.getId(), order.getId());
        }
    }

    // Complete order (stock already reserved, just record completion)
    public void completeOrder(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Long menuItemId = orderItem.getMenuItem().getId();
            Integer quantity = orderItem.getQuantity();
            
            Inventory inventory = getInventoryByMenuItemId(menuItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found for menu item: " + orderItem.getMenuItem().getName()));

            // Record completion transaction
            recordTransaction(inventory, InventoryTransaction.TransactionType.ORDER_COMPLETED, 
                            quantity, inventory.getCurrentStock(), inventory.getCurrentStock(), null, 
                            "Order #" + order.getId() + " completed", order.getId());
        }
    }

    // Cancel order and restore stock
    public void cancelOrder(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Long menuItemId = orderItem.getMenuItem().getId();
            Integer quantity = orderItem.getQuantity();
            
            Inventory inventory = getInventoryByMenuItemId(menuItemId)
                    .orElseThrow(() -> new IllegalArgumentException("Inventory not found for menu item: " + orderItem.getMenuItem().getName()));

            int previousStock = inventory.getCurrentStock();
            inventory.addStock(quantity); // Restore the reserved stock
            inventoryRepository.save(inventory);
            
            // Record transaction
            recordTransaction(inventory, InventoryTransaction.TransactionType.ORDER_CANCELLED, 
                            quantity, previousStock, inventory.getCurrentStock(), null, 
                            "Order #" + order.getId() + " cancelled - stock restored", order.getId());
        }
    }

    // Check if order can be fulfilled
    public boolean canFulfillOrder(Order order) {
        for (OrderItem orderItem : order.getOrderItems()) {
            Long menuItemId = orderItem.getMenuItem().getId();
            Integer quantity = orderItem.getQuantity();
            
            Optional<Inventory> inventoryOpt = getInventoryByMenuItemId(menuItemId);
            if (inventoryOpt.isEmpty()) {
                return false; // No inventory record exists
            }
            
            Inventory inventory = inventoryOpt.get();
            if (!inventory.canReserve(quantity)) {
                return false; // Insufficient stock
            }
        }
        return true;
    }

    // Get available menu items (with stock)
    public List<MenuItem> getAvailableMenuItems() {
        return menuActivityService.getAllMenuItems().stream()
                .filter(item -> {
                    Optional<Inventory> inventory = getInventoryByMenuItemId(item.getId());
                    return inventory.isPresent() && !inventory.get().isOutOfStock();
                })
                .toList();
    }

    // Update inventory settings
    public Inventory updateInventorySettings(Long menuItemId, Integer minimumStock, Integer maximumStock) {
        Inventory inventory = getInventoryByMenuItemId(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for menu item"));

        inventory.setMinimumStock(minimumStock);
        inventory.setMaximumStock(maximumStock);
        
        return inventoryRepository.save(inventory);
    }

    // Get transaction history
    public List<InventoryTransaction> getTransactionHistory(Long inventoryId) {
        return transactionRepository.findByInventoryIdOrderByTransactionDateDesc(inventoryId);
    }

    public List<InventoryTransaction> getAllTransactions() {
        return transactionRepository.findAllOrderByTransactionDateDesc();
    }

    public List<InventoryTransaction> getTransactionsByOrderId(Long orderId) {
        return transactionRepository.findByOrderId(orderId);
    }

    // Private method to record transactions
    private void recordTransaction(Inventory inventory, InventoryTransaction.TransactionType type, 
                                 Integer quantity, Integer previousStock, Integer newStock, 
                                 Users user, String notes, Long orderId) {
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setInventory(inventory);
        transaction.setTransactionType(type);
        transaction.setQuantity(quantity);
        transaction.setPreviousStock(previousStock);
        transaction.setNewStock(newStock);
        transaction.setNotes(notes);
        transaction.setUser(user);
        transaction.setOrderId(orderId);
        
        transactionRepository.save(transaction);
    }
} 