package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.repository.MenuItemRepository;
import com.ferdican.restaurantsystem.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final UsersService usersService;

    @Autowired
    public MenuController(MenuItemRepository menuItemRepository, UsersService usersService) {
        this.menuItemRepository = menuItemRepository;
        this.usersService = usersService;
    }

    @GetMapping
    public String showMenu(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll());
        
        // Kullanıcı bilgilerini ekle
        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("user", currentUserProfile);
        
        return "menu";
    }
}
