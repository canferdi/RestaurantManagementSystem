package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Waiter;
import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.services.WaiterService;
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
@RequestMapping("waiter_page")
public class WaiterProfileController {

    private final UsersRepository usersRepository;
    private final WaiterService waiterService;

    public WaiterProfileController(UsersRepository usersRepository, WaiterService waiterService) {
        this.usersRepository = usersRepository;
        this.waiterService = waiterService;
    }

    @GetMapping("/")
    public String WaiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() ->
                    new UsernameNotFoundException("Could not found user"));
            Optional<Waiter> waiterProfile = waiterService.getOne(users.getUserId());

            if (waiterProfile.isPresent()) {
                model.addAttribute("profile", waiterProfile.get());
            }
        }
        return "redirect:/dashboard/";
    }
} 