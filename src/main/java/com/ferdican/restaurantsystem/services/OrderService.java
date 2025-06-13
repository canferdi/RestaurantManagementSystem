package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Order;
import com.ferdican.restaurantsystem.entity.OrderStatus;
import com.ferdican.restaurantsystem.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    @Autowired
    public OrderService(OrderRepository orderRepository, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public Order createOrder(Order order) {
        // Check if order can be fulfilled before creating
        if (!inventoryService.canFulfillOrder(order)) {
            throw new IllegalStateException("Cannot create order: insufficient stock for some items");
        }

        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        
        // Save order first to get ID
        Order savedOrder = orderRepository.save(order);
        
        // Reserve stock for the order
        try {
            inventoryService.reserveStockForOrder(savedOrder);
        } catch (Exception e) {
            // If stock reservation fails, delete the order and throw exception
            orderRepository.delete(savedOrder);
            throw new IllegalStateException("Failed to reserve stock: " + e.getMessage());
        }
        
        return savedOrder;
    }

    @Transactional
    public boolean updateOrderStatus(Long id, OrderStatus newStatus) {
        try {
            return orderRepository.findById(id)
                    .map(order -> {
                        OrderStatus oldStatus = order.getStatus();
                        order.setStatus(newStatus);
                        orderRepository.save(order);
                        
                        // Handle inventory based on status change
                        if (newStatus == OrderStatus.COMPLETED && oldStatus != OrderStatus.COMPLETED) {
                            inventoryService.completeOrder(order);
                        } else if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
                            inventoryService.cancelOrder(order);
                        }
                        
                        return true;
                    })
                    .orElse(false);
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }

    public List<Order> getOrders(OrderStatus status, Date dateFrom, Date dateTo) {
        try {
            return orderRepository.findOrdersByFilters(status, dateFrom, dateTo);
        } catch (Exception e) {
            // Log the error and return empty list
            System.err.println("Error fetching orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> getAllOrders() {
        try {
            return orderRepository.findAllOrdersOrderByDate();
        } catch (Exception e) {
            System.err.println("Error fetching all orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> getActiveOrders() {
        try {
            return orderRepository.findByStatus(OrderStatus.PENDING);
        } catch (Exception e) {
            System.err.println("Error fetching active orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> getPendingOrders() {
        try {
            return orderRepository.findByStatus(OrderStatus.PENDING);
        } catch (Exception e) {
            System.err.println("Error fetching pending orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> getPreparingOrders() {
        try {
            return orderRepository.findByStatus(OrderStatus.PREPARING);
        } catch (Exception e) {
            System.err.println("Error fetching preparing orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> getReadyOrders() {
        try {
            return orderRepository.findByStatus(OrderStatus.READY);
        } catch (Exception e) {
            System.err.println("Error fetching ready orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public List<Order> getDeliveredOrders() {
        try {
            return orderRepository.findByStatus(OrderStatus.DELIVERED);
        } catch (Exception e) {
            System.err.println("Error fetching delivered orders: " + e.getMessage());
            return new java.util.ArrayList<>();
        }
    }

    public Optional<Order> getOrderById(Long id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error fetching order by id: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    public boolean cancelOrder(Long orderId) {
        try {
            return orderRepository.findById(orderId)
                    .map(order -> {
                        if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.PREPARING) {
                            order.setStatus(OrderStatus.CANCELLED);
                            orderRepository.save(order);
                            
                            // Restore stock
                            inventoryService.cancelOrder(order);
                            return true;
                        }
                        return false; // Cannot cancel completed/delivered orders
                    })
                    .orElse(false);
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }
} 