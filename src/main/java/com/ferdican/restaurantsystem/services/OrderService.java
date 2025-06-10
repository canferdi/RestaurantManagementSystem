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
        return orderRepository.findOrdersByFilters(status, dateFrom, dateTo);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional
    public boolean updateOrderStatus(Long id, OrderStatus newStatus) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(newStatus);
                    orderRepository.save(order);
                    return true;
                })
                .orElse(false);
    }
} 