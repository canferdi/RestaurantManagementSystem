package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.RestorantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TableRepository extends JpaRepository<RestorantTable, Long> {
    List<RestorantTable> findByWaiter_UserAccountId(Long waiterId);
} 