package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.Order;
import com.ferdican.restaurantsystem.entity.OrderStatus;
import com.ferdican.restaurantsystem.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o WHERE " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:dateFrom IS NULL OR o.orderDate >= :dateFrom) AND " +
           "(:dateTo IS NULL OR o.orderDate <= :dateTo) " +
           "ORDER BY o.orderDate DESC")
    List<Order> findOrdersByFilters(
            @Param("status") OrderStatus status,
            @Param("dateFrom") Date dateFrom,
            @Param("dateTo") Date dateTo);

    List<Order> findByStatus(OrderStatus orderStatus);
    
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrdersOrderByDate();
    
    // Find orders by user
    List<Order> findByUserOrderByOrderDateDesc(Users user);
    
    // Find orders by user and status
    List<Order> findByUserAndStatusOrderByOrderDateDesc(Users user, OrderStatus status);
}