package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.AdminProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminProfileRepository extends JpaRepository<AdminProfile, Long> {
}
