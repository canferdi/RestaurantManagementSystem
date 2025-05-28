package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.repository.MenuItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MenuController {

    MenuItemRepository menuItemRepository;

    public MenuController(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @GetMapping("/")
    public String showMenu(Model model) {
        model.addAttribute("menuItems", menuItemRepository.findAll());

        return "menu"; // Assuming there is a Thymeleaf template named 'menu.html'
    }


}
