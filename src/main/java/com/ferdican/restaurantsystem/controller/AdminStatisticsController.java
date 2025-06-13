package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.MenuItem;
import com.ferdican.restaurantsystem.entity.OrderItem;
import com.ferdican.restaurantsystem.repository.MenuItemRepository;
import com.ferdican.restaurantsystem.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;

@Controller
@RequestMapping("/admin/statistics")
@PreAuthorize("hasAuthority('admin')")
public class AdminStatisticsController {
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private MenuItemRepository menuItemRepository;

    @GetMapping
    public String statistics(Model model) {
        List<MenuItem> menuItems = menuItemRepository.findAll();
        List<OrderItem> orderItems = orderItemRepository.findAll();
        Map<Long, Integer> itemSales = new HashMap<>();
        Map<Long, Double> itemRevenue = new HashMap<>();
        for (MenuItem item : menuItems) {
            itemSales.put(item.getId(), 0);
            itemRevenue.put(item.getId(), 0.0);
        }
        for (OrderItem orderItem : orderItems) {
            Long id = orderItem.getMenuItem().getId();
            int qty = orderItem.getQuantity() != null ? orderItem.getQuantity() : 0;
            double total = (orderItem.getPrice() != null ? orderItem.getPrice() : 0.0) * qty;
            itemSales.put(id, itemSales.getOrDefault(id, 0) + qty);
            itemRevenue.put(id, itemRevenue.getOrDefault(id, 0.0) + total);
        }
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("itemSales", itemSales);
        model.addAttribute("itemRevenue", itemRevenue);
        return "admin/statistics";
    }
} 