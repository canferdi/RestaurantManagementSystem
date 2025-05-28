package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.UsersType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTypeRepository extends JpaRepository<UsersType, Long> {
}
