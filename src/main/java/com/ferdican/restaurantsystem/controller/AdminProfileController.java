package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Admin;
import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.services.AdminService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("admin_page")
public class AdminProfileController {

    private final UsersRepository usersRepository;
    private final AdminService adminService;

    public AdminProfileController(UsersRepository usersRepository, AdminService adminService) {
        this.usersRepository = usersRepository;
        this.adminService = adminService;
    }

    @GetMapping("/")
    public String AdminProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new
                    UsernameNotFoundException("Could not found user"));
            Optional<Admin> adminProfile = adminService.getOne(users.getUserId());

            if (!adminProfile.isEmpty()) {
                model.addAttribute("profile", adminProfile.get());
            }
        }
        return "redirect:/dashboard/";
    }

    /*
    @GetMapping("/admin_menu /admin_menu/")
    public String adminMenu() {
        return "admin_menu";
    }
    */



}
