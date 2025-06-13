package com.ferdican.restaurantsystem.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "menu_item_id", unique = true)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer minimumStock;

    @Column(nullable = false)
    private Integer maximumStock;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdated = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    // Helper methods
    public boolean isLowStock() {
        return currentStock <= minimumStock;
    }

    public boolean isOutOfStock() {
        return currentStock <= 0;
    }

    public boolean canReserve(int quantity) {
        return currentStock >= quantity;
    }

    public void reserveStock(int quantity) {
        if (canReserve(quantity)) {
            this.currentStock -= quantity;
        } else {
            throw new IllegalStateException("Insufficient stock. Available: " + currentStock + ", Requested: " + quantity);
        }
    }

    public void addStock(int quantity) {
        if (this.currentStock + quantity > maximumStock) {
            throw new IllegalStateException("Cannot add stock. Would exceed maximum stock of " + maximumStock);
        }
        this.currentStock += quantity;
    }

    public void removeStock(int quantity) {
        if (this.currentStock - quantity < 0) {
            throw new IllegalStateException("Cannot remove stock. Would result in negative stock");
        }
        this.currentStock -= quantity;
    }
} 