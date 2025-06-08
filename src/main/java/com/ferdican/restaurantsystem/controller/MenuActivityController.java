package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.services.MenuActivityService;
import com.ferdican.restaurantsystem.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuActivityController {

    private final UsersService usersService;
    private final MenuActivityService menuActivityService;

    @Autowired
    public MenuActivityController(UsersService usersService, MenuActivityService menuActivityService) {
        this.usersService = usersService;
        this.menuActivityService = menuActivityService;
    }

    @GetMapping("/dashboard/")
    public String searchMenuItems(Model model) {

        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
        }
        model.addAttribute("user", currentUserProfile);
        return "dashboard";
    }


}
