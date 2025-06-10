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
            System.out.println("Sipariş oluşturma isteği alındı: " + orderData);

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Users user = usersRepository.findByEmail(auth.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));

            Order order = new Order();
            order.setUser(user);
            order.setDeliveryAddress((String) orderData.get("deliveryAddress"));
            order.setNotes((String) orderData.get("notes"));
            order.setTotalAmount(Double.valueOf(orderData.get("totalAmount").toString()));
            order.setOrderItems(new ArrayList<>()); // Listeyi başlat

            // OrderItem'ları oluştur
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
            for (Map<String, Object> item : items) {
                OrderItem orderItem = new OrderItem();
                Long menuItemId = Long.valueOf(item.get("menuItemId").toString());
                MenuItem menuItem = menuItemRepository.findById(menuItemId)
                        .orElseThrow(() -> new RuntimeException("Menu item bulunamadı: " + menuItemId));
                orderItem.setMenuItem(menuItem);
                orderItem.setQuantity(Integer.valueOf(item.get("quantity").toString()));
                orderItem.setPrice(Double.valueOf(item.get("price").toString()));
                orderItem.setOrder(order);
                order.getOrderItems().add(orderItem);
            }

            Order savedOrder = orderService.createOrder(order);
            System.out.println("Sipariş başarıyla oluşturuldu: " + savedOrder.getId());
            return ResponseEntity.ok(savedOrder);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Sipariş oluşturulurken bir hata oluştu: " + e.getMessage());
        }
    }

    @GetMapping("/my-orders")
    public String getMyOrders(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = usersRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı"));

        List<Order> orders = orderService.getOrders(null, null, null).stream()
                .filter(order -> order.getUser().getUserId().equals(user.getUserId()))
                .toList();

        model.addAttribute("orders", orders);
        return "customer/my_orders";
    }
} 