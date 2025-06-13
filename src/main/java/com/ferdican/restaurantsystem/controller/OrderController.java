package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Order;
import com.ferdican.restaurantsystem.entity.OrderStatus;
import com.ferdican.restaurantsystem.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getAllOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date dateTo,
            Model model) {
        
        try {
            List<Order> allOrders;
            if (status != null || dateFrom != null || dateTo != null) {
                allOrders = orderService.getOrders(status, dateFrom, dateTo);
            } else {
                allOrders = orderService.getAllOrders();
            }
            
            // Get orders by status using service methods
            List<Order> pendingOrders = orderService.getPendingOrders();
            List<Order> preparingOrders = orderService.getPreparingOrders();
            List<Order> readyOrders = orderService.getReadyOrders();
            List<Order> deliveredOrders = orderService.getDeliveredOrders();
            
            model.addAttribute("orders", allOrders);
            model.addAttribute("pendingOrders", pendingOrders);
            model.addAttribute("preparingOrders", preparingOrders);
            model.addAttribute("readyOrders", readyOrders);
            model.addAttribute("deliveredOrders", deliveredOrders);
            
            return "orders/all_orders";
        } catch (Exception e) {
            model.addAttribute("error", "Error occurred while loading orders: " + e.getMessage());
            model.addAttribute("orders", new java.util.ArrayList<>());
            model.addAttribute("pendingOrders", new java.util.ArrayList<>());
            model.addAttribute("preparingOrders", new java.util.ArrayList<>());
            model.addAttribute("readyOrders", new java.util.ArrayList<>());
            model.addAttribute("deliveredOrders", new java.util.ArrayList<>());
            return "orders/all_orders";
        }
    }

    @GetMapping("/{id}/details")
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

    @PostMapping("/{id}/status")
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
} 