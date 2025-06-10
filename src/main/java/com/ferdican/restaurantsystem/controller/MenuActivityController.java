package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.MenuItem;
import com.ferdican.restaurantsystem.services.MenuActivityService;
import com.ferdican.restaurantsystem.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dashboard")
public class MenuActivityController {

    private final UsersService usersService;
    private final MenuActivityService menuActivityService;

    @Autowired
    public MenuActivityController(UsersService usersService, MenuActivityService menuActivityService) {
        this.usersService = usersService;
        this.menuActivityService = menuActivityService;
    }

    @GetMapping("/")
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

    @GetMapping("/manage")
    public String manageMenu(Model model) {
        model.addAttribute("menuItems", menuActivityService.getAllMenuItems());
        model.addAttribute("newMenuItem", new MenuItem());
        return "admin/manage_menu";
    }

    @GetMapping("/item/{id}")
    @ResponseBody
    public ResponseEntity<MenuItem> getMenuItem(@PathVariable Long id) {
        return menuActivityService.getMenuItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/add")
    public String addMenuItem(@ModelAttribute MenuItem menuItem) {
        menuItem.setAvailable(true);
        menuActivityService.addNew(menuItem);
        return "redirect:/dashboard/manage";
    }

    @PostMapping("/update/{id}")
    public String updateMenuItem(@PathVariable Long id, @ModelAttribute MenuItem menuItem) {
        menuItem.setId(id);
        menuActivityService.updateMenuItem(menuItem);
        return "redirect:/dashboard/manage";
    }

    @PostMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Long id) {
        menuActivityService.deleteMenuItem(id);
        return "redirect:/dashboard/manage";
    }
}
