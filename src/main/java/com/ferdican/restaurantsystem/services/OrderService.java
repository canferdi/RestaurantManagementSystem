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

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        order.setOrderDate(new Date());
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
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

    public Optional<Order> getOrderById(Long id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            System.err.println("Error fetching order by id: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Transactional
    public boolean updateOrderStatus(Long id, OrderStatus newStatus) {
        try {
            return orderRepository.findById(id)
                    .map(order -> {
                        order.setStatus(newStatus);
                        orderRepository.save(order);
                        return true;
                    })
                    .orElse(false);
        } catch (Exception e) {
            System.err.println("Error updating order status: " + e.getMessage());
            return false;
        }
    }
} 