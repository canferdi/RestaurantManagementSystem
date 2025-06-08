package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.MenuItem;
import com.ferdican.restaurantsystem.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuActivityService {

    private final MenuItemRepository menuItemRepository;

    @Autowired
    public MenuActivityService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public MenuItem addNew(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }



}
