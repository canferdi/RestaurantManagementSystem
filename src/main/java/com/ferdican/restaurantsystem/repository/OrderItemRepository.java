package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.OrderItem;
import com.ferdican.restaurantsystem.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi.menuItem, SUM(oi.quantity) as totalSold FROM OrderItem oi GROUP BY oi.menuItem ORDER BY totalSold DESC")
    List<Object[]> findTopPopularMenuItems(org.springframework.data.domain.Pageable pageable);

    default List<Object[]> findTopPopularMenuItems(int limit) {
        return findTopPopularMenuItems(org.springframework.data.domain.PageRequest.of(0, limit));
    }
} 