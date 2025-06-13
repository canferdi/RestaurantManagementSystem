package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    public Optional<Users> findByEmail(String email);

    java.util.List<Users> findByUserTypeId_UserTypeName(String userTypeName);
}
