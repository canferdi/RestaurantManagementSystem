package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Order;
import com.ferdican.restaurantsystem.services.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/waiter/orders")
public class WaiterOrderController {

    OrderService orderService;

    public WaiterOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping()
    public String getActiveOrders(Model model) {
        // Aktif siparişleri ve masaları ekle (örnek veri, gerçek servis ile doldurulmalı)
        // model.addAttribute("orders", orderService.getActiveOrdersForWaiter(...));
        // model.addAttribute("tables", tableService.getAllTablesForWaiter(...));
        return "dashboard";
    }

    @GetMapping("/by-table")
    public String getOrdersByTable(@RequestParam Integer tableNumber, Model model) {
        return null;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long id) {
        return null;
    }

    /*
    @PostMapping("/{id}/status")
    @ResponseBody
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        // PENDING -> PREPARING -> READY -> SERVED
        return null;
    }
    */








}
