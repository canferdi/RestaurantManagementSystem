package com.ferdican.restaurantsystem.repository;

import com.ferdican.restaurantsystem.entity.WorkSchedule;
import com.ferdican.restaurantsystem.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    List<WorkSchedule> findByUser(Users user);
    List<WorkSchedule> findAllByOrderByUser_UserIdAsc();
} 