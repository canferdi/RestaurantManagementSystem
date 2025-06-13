package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Order;
import com.ferdican.restaurantsystem.entity.OrderStatus;
import com.ferdican.restaurantsystem.services.OrderService;
import com.ferdican.restaurantsystem.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {

    private final OrderService orderService;
    private final UsersService usersService;

    @Autowired
    public KitchenController(OrderService orderService, UsersService usersService) {
        this.orderService = orderService;
        this.usersService = usersService;
    }

    @GetMapping("/dashboard")
    public String kitchenDashboard(Model model) {
        // Get current user info
        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        model.addAttribute("username", currentUsername);
        model.addAttribute("user", currentUserProfile);
        
        // Get orders that need preparation
        List<Order> pendingOrders = orderService.getOrders(OrderStatus.PENDING, null, null);
        List<Order> preparingOrders = orderService.getOrders(OrderStatus.PREPARING, null, null);
        List<Order> readyOrders = orderService.getOrders(OrderStatus.READY_FOR_DELIVERY, null, null);
        
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("preparingOrders", preparingOrders);
        model.addAttribute("readyOrders", readyOrders);
        
        return "kitchen/dashboard";
    }

    @PostMapping("/orders/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        try {
            OrderStatus newStatus = OrderStatus.valueOf(request.get("status"));
            if (orderService.updateOrderStatus(id, newStatus)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/orders/{id}/details")
    @ResponseBody
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long id) {
        try {
            return orderService.getOrderById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/orders")
    public String getAllOrders(Model model) {
        try {
            // Get orders by status using service methods
            List<Order> pendingOrders = orderService.getPendingOrders();
            List<Order> preparingOrders = orderService.getPreparingOrders();
            List<Order> readyOrders = orderService.getReadyOrders();
            List<Order> deliveredOrders = orderService.getDeliveredOrders();
            
            model.addAttribute("pendingOrders", pendingOrders);
            model.addAttribute("preparingOrders", preparingOrders);
            model.addAttribute("readyOrders", readyOrders);
            model.addAttribute("deliveredOrders", deliveredOrders);
            
            return "orders/all_orders";
        } catch (Exception e) {
            model.addAttribute("error", "Error occurred while loading orders: " + e.getMessage());
            model.addAttribute("pendingOrders", new java.util.ArrayList<>());
            model.addAttribute("preparingOrders", new java.util.ArrayList<>());
            model.addAttribute("readyOrders", new java.util.ArrayList<>());
            model.addAttribute("deliveredOrders", new java.util.ArrayList<>());
            return "orders/all_orders";
        }
    }
}
