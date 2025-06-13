package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.entity.WorkSchedule;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.repository.WorkScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/schedule")
public class ScheduleController {
    @Autowired
    private WorkScheduleRepository workScheduleRepository;
    @Autowired
    private UsersRepository usersRepository;

    @GetMapping
    public String viewSchedule(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"));

        // Tüm waiter ve kitchen staff kullanıcılarını çek
        List<Users> waiters = usersRepository.findByUserTypeId_UserTypeName("waiter");
        List<Users> kitchenStaff = usersRepository.findByUserTypeId_UserTypeName("kitchen staff");
        List<Users> allStaff = new java.util.ArrayList<>();
        allStaff.addAll(waiters);
        allStaff.addAll(kitchenStaff);

        // Her biri için schedule varsa getir, yoksa boş schedule oluştur
        List<WorkSchedule> schedules = new java.util.ArrayList<>();
        for (Users staff : allStaff) {
            List<WorkSchedule> userSchedules = workScheduleRepository.findByUser(staff);
            if (userSchedules.isEmpty()) {
                WorkSchedule emptySchedule = new WorkSchedule();
                emptySchedule.setUser(staff);
                emptySchedule.setWorkDays(new java.util.ArrayList<>());
                schedules.add(emptySchedule);
            } else {
                schedules.addAll(userSchedules);
            }
        }
        model.addAttribute("schedules", schedules);
        model.addAttribute("isAdmin", isAdmin);
        return "schedule/schedule";
    }

    @PostMapping("/update")
    public String updateSchedule(@ModelAttribute WorkSchedule schedule, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("admin"));
        if (!isAdmin) {
            return "redirect:/schedule?error=not_authorized";
        }
        if (schedule.getUser() != null && schedule.getUser().getUserId() != null) {
            if (schedule.getWorkDays() == null) {
                schedule.setWorkDays(new java.util.ArrayList<>());
            }
            workScheduleRepository.save(schedule);
        }
        return "redirect:/schedule?success";
    }
} 