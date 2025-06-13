package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.MenuItem;
import com.ferdican.restaurantsystem.repository.MenuItemRepository;
import com.ferdican.restaurantsystem.repository.OrderItemRepository;
import com.ferdican.restaurantsystem.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuController {

    private final MenuItemRepository menuItemRepository;
    private final UsersService usersService;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public MenuController(MenuItemRepository menuItemRepository, UsersService usersService, OrderItemRepository orderItemRepository) {
        this.menuItemRepository = menuItemRepository;
        this.usersService = usersService;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping
    public String showMenu(Model model) {
        List<Object[]> popular = orderItemRepository.findTopPopularMenuItems(4);
        List<MenuItem> popularItems = new java.util.ArrayList<>();
        List<Long> popularIds = new java.util.ArrayList<>();
        for (Object[] row : popular) {
            MenuItem mi = (MenuItem) row[0];
            popularItems.add(mi);
            popularIds.add(mi.getId());
        }
        model.addAttribute("popularItems", popularItems);
        model.addAttribute("popularIds", popularIds);
        // Popüler ürünler hariç menü
        List<MenuItem> allItems = menuItemRepository.findAll();
        List<MenuItem> menuItems = new java.util.ArrayList<>();
        for (MenuItem mi : allItems) {
            if (!popularIds.contains(mi.getId())) {
                menuItems.add(mi);
            }
        }
        model.addAttribute("menuItems", menuItems);
        
        // Add user information
        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (!authentication.getName().equals("anonymousUser")) {
            model.addAttribute("username", authentication.getName());
        }
        model.addAttribute("user", currentUserProfile);
        
        return "menu";
    }
}
