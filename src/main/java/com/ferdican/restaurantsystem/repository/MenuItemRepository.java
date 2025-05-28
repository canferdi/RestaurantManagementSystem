package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
