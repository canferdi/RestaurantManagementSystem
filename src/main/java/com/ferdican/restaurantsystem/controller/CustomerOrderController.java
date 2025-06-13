package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.*;
import com.ferdican.restaurantsystem.repository.MenuItemRepository;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.services.OrderService;
import com.ferdican.restaurantsystem.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/orders")
public class CustomerOrderController {

    private final OrderService orderService;
    private final UsersRepository usersRepository;
    private final UsersService usersService;
    private final MenuItemRepository menuItemRepository;

    @Autowired
    public CustomerOrderController(OrderService orderService,
                                   UsersRepository usersRepository,
                                   UsersService usersService,
                                   MenuItemRepository menuItemRepository) {
        this.orderService = orderService;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
        this.menuItemRepository = menuItemRepository;
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> orderData) {
        try {
            System.out.println("Order creation request received: " + orderData);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Users user = usersRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Order order = new Order();
            order.setUser(user);
            order.setDeliveryAddress((String) orderData.get("deliveryAddress"));
            order.setNotes((String) orderData.get("notes"));
            order.setTotalAmount(Double.valueOf(orderData.get("totalAmount").toString()));

            // Create OrderItems
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            for (Map<String, Object> item : items) {
                OrderItem orderItem = new OrderItem();
                Long menuItemId = Long.valueOf(item.get("menuItemId").toString());
                MenuItem menuItem = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new RuntimeException("Menu item not found: " + menuItemId));
                orderItem.setMenuItem(menuItem);
                orderItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
                orderItem.setPrice(Double.valueOf(item.get("price").toString()));
                orderItem.setOrder(order); // Set Order to OrderItem
                order.getOrderItems().add(orderItem); // Add OrderItem to Order
            }

            Order savedOrder = orderService.createOrder(order);
            System.out.println("Order successfully created: " + savedOrder.getId());
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error occurred while creating order: " + e.getMessage());
        }
    }

    @GetMapping("/my-orders")
    public String getMyOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Use the new service method to get orders by user
        List<Order> orders = orderService.getOrdersByUser(user);

        model.addAttribute("orders", orders);
        return "customer/my_orders";
    }

    @GetMapping
    public String getOrders(Model model) {
        // Redirect to my-orders for customers
        return "redirect:/customer/orders/my-orders";
    }

    @GetMapping("/{id}/details")
    @ResponseBody
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Users user = usersRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return orderService.getOrderById(id)
                    .filter(order -> order.getUser() != null && order.getUser().getUserId().equals(user.getUserId()))
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
} 