package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
