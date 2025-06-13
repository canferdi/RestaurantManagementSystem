package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.*;
import com.ferdican.restaurantsystem.services.InventoryService;
import com.ferdican.restaurantsystem.services.MenuActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;
    private final MenuActivityService menuActivityService;

    @Autowired
    public AdminInventoryController(InventoryService inventoryService, MenuActivityService menuActivityService) {
        this.inventoryService = inventoryService;
        this.menuActivityService = menuActivityService;
    }

    @GetMapping
    public String inventoryManagement(Model model) {
        try {
            List<Inventory> allInventory = inventoryService.getAllInventory();
            List<Inventory> lowStockItems = inventoryService.getLowStockItems();
            List<Inventory> outOfStockItems = inventoryService.getOutOfStockItems();
            List<MenuItem> menuItemsWithoutInventory = getMenuItemsWithoutInventory();
            
            model.addAttribute("allInventory", allInventory);
            model.addAttribute("lowStockItems", lowStockItems);
            model.addAttribute("outOfStockItems", outOfStockItems);
            model.addAttribute("menuItemsWithoutInventory", menuItemsWithoutInventory);
            
            return "admin/inventory";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading inventory: " + e.getMessage());
            return "admin/inventory";
        }
    }

    @GetMapping("/transactions")
    public String inventoryTransactions(Model model) {
        try {
            List<InventoryTransaction> transactions = inventoryService.getAllTransactions();
            model.addAttribute("transactions", transactions);
            return "admin/inventory-transactions";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading transactions: " + e.getMessage());
            return "admin/inventory-transactions";
        }
    }

    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createInventory(@RequestBody Map<String, Object> request) {
        try {
            Long menuItemId = Long.parseLong(request.get("menuItemId").toString());
            Integer currentStock = Integer.parseInt(request.get("currentStock").toString());
            Integer minimumStock = Integer.parseInt(request.get("minimumStock").toString());
            Integer maximumStock = Integer.parseInt(request.get("maximumStock").toString());

            Inventory inventory = inventoryService.createInventory(menuItemId, currentStock, minimumStock, maximumStock);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Inventory created successfully",
                "inventoryId", inventory.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error creating inventory: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/add-stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addStock(@RequestBody Map<String, Object> request) {
        try {
            Long menuItemId = Long.parseLong(request.get("menuItemId").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            String notes = request.get("notes").toString();
            // TODO: Get current user from security context
            Users user = null; // This should be injected from security context

            Inventory inventory = inventoryService.addStock(menuItemId, quantity, user, notes);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Stock added successfully",
                "newStock", inventory.getCurrentStock()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error adding stock: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/remove-stock")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeStock(@RequestBody Map<String, Object> request) {
        try {
            Long menuItemId = Long.parseLong(request.get("menuItemId").toString());
            Integer quantity = Integer.parseInt(request.get("quantity").toString());
            String notes = request.get("notes").toString();
            // TODO: Get current user from security context
            Users user = null; // This should be injected from security context

            Inventory inventory = inventoryService.removeStock(menuItemId, quantity, user, notes);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Stock removed successfully",
                "newStock", inventory.getCurrentStock()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error removing stock: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/update-settings")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateInventorySettings(@RequestBody Map<String, Object> request) {
        try {
            Long menuItemId = Long.parseLong(request.get("menuItemId").toString());
            Integer minimumStock = Integer.parseInt(request.get("minimumStock").toString());
            Integer maximumStock = Integer.parseInt(request.get("maximumStock").toString());

            Inventory inventory = inventoryService.updateInventorySettings(menuItemId, minimumStock, maximumStock);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Inventory settings updated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error updating settings: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/api/items-without-inventory")
    @ResponseBody
    public ResponseEntity<List<MenuItem>> getMenuItemsWithoutInventoryAPI() {
        List<MenuItem> items = getMenuItemsWithoutInventory();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/api/low-stock")
    @ResponseBody
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        List<Inventory> lowStockItems = inventoryService.getLowStockItems();
        return ResponseEntity.ok(lowStockItems);
    }

    @GetMapping("/api/out-of-stock")
    @ResponseBody
    public ResponseEntity<List<Inventory>> getOutOfStockItems() {
        List<Inventory> outOfStockItems = inventoryService.getOutOfStockItems();
        return ResponseEntity.ok(outOfStockItems);
    }

    private List<MenuItem> getMenuItemsWithoutInventory() {
        List<MenuItem> allMenuItems = menuActivityService.getAllMenuItems();
        return allMenuItems.stream()
                .filter(item -> inventoryService.getInventoryByMenuItemId(item.getId()).isEmpty())
                .collect(Collectors.toList());
    }
} 