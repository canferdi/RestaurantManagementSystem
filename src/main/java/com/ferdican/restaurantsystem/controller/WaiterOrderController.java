package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Order;
import com.ferdican.restaurantsystem.entity.MenuItem;
import com.ferdican.restaurantsystem.entity.OrderItem;
import com.ferdican.restaurantsystem.entity.RestorantTable;
import com.ferdican.restaurantsystem.services.OrderService;
import com.ferdican.restaurantsystem.services.TableService;
import com.ferdican.restaurantsystem.services.MenuActivityService;
import com.ferdican.restaurantsystem.services.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Date;

@Controller
@RequestMapping("/waiter")
public class WaiterOrderController {

    private final OrderService orderService;
    private final TableService tableService;
    private final MenuActivityService menuActivityService;
    private final InventoryService inventoryService;

    @Autowired
    public WaiterOrderController(OrderService orderService, TableService tableService, 
                               MenuActivityService menuActivityService, InventoryService inventoryService) {
        this.orderService = orderService;
        this.tableService = tableService;
        this.menuActivityService = menuActivityService;
        this.inventoryService = inventoryService;
    }

    @GetMapping("/dashboard")
    public String waiterDashboard(Model model) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        // Get tables and log their status for debugging
        List<RestorantTable> tables = tableService.getAllTables();
        System.out.println("=== TABLE STATUS DEBUG ===");
        for (RestorantTable table : tables) {
            System.out.println("Table " + table.getNumber() + ": " + table.getStatus() + " (name: " + table.getStatus().name() + ")");
        }
        System.out.println("=========================");
        
        // Get orders and format their dates
        List<Order> pendingOrders = orderService.getPendingOrders();
        List<Order> readyOrders = orderService.getReadyOrders();
        
        // Create formatted order data
        List<Map<String, Object>> formattedPendingOrders = pendingOrders.stream()
            .map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("tableNumber", order.getTable() != null ? order.getTable().getNumber() : "N/A");
                orderMap.put("itemCount", order.getOrderItems().size());
                orderMap.put("formattedTime", order.getOrderDate() != null ? timeFormat.format(order.getOrderDate()) : "N/A");
                return orderMap;
            })
            .collect(Collectors.toList());
            
        List<Map<String, Object>> formattedReadyOrders = readyOrders.stream()
            .map(order -> {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("id", order.getId());
                orderMap.put("tableNumber", order.getTable() != null ? order.getTable().getNumber() : "N/A");
                orderMap.put("itemCount", order.getOrderItems().size());
                orderMap.put("formattedTime", order.getOrderDate() != null ? timeFormat.format(order.getOrderDate()) : "N/A");
                return orderMap;
            })
            .collect(Collectors.toList());
        
        // Add data to model
        model.addAttribute("tables", tables);
        model.addAttribute("pendingOrders", formattedPendingOrders);
        model.addAttribute("readyOrders", formattedReadyOrders);
        
        return "waiter/dashboard";
    }

    @GetMapping("/menu-items")
    @ResponseBody
    public ResponseEntity<List<MenuItem>> getMenuItems() {
        // Only return menu items that have stock available
        List<MenuItem> availableMenuItems = inventoryService.getAvailableMenuItems();
        return ResponseEntity.ok(availableMenuItems);
    }

    @PostMapping("/create-order")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> orderRequest) {
        try {
            // Extract order data
            Long tableId = Long.parseLong(orderRequest.get("tableId").toString());
            String tableNumber = orderRequest.get("tableNumber").toString();
            List<Map<String, Object>> items = (List<Map<String, Object>>) orderRequest.get("items");
            String paymentMethod = orderRequest.get("paymentMethod").toString();
            String notes = orderRequest.get("notes").toString();
            Double totalAmount = Double.parseDouble(orderRequest.get("totalAmount").toString());

            // Get table
            RestorantTable table = tableService.getTableById(tableId);
            if (table == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Table not found"));
            }

            // Create order
            Order order = new Order();
            order.setTable(table);
            order.setTotalAmount(totalAmount);
            order.setOrderDate(new Date());
            order.setNotes(notes);
            order.setStatus(com.ferdican.restaurantsystem.entity.OrderStatus.PENDING);

            // Create order items
            List<OrderItem> orderItems = items.stream()
                .map(item -> {
                    Long menuItemId = Long.parseLong(item.get("id").toString());
                    Integer quantity = Integer.parseInt(item.get("quantity").toString());
                    
                    MenuItem menuItem = menuActivityService.getMenuItemById(menuItemId).orElse(null);
                    if (menuItem == null) {
                        throw new RuntimeException("Menu item not found: " + menuItemId);
                    }
                    
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setMenuItem(menuItem);
                    orderItem.setQuantity(quantity);
                    orderItem.setPrice(menuItem.getPrice());
                    
                    return orderItem;
                })
                .collect(Collectors.toList());

            order.setOrderItems(orderItems);

            // Save order (this will check stock and reserve it)
            Order savedOrder = orderService.createOrder(order);

            // Update table status to OCCUPIED
            table.setStatus(com.ferdican.restaurantsystem.entity.TableStatus.OCCUPIED);
            tableService.save(table);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "orderId", savedOrder.getId(),
                "message", "Order created successfully"
            ));

        } catch (IllegalStateException e) {
            // Stock-related error
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Stock error: " + e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Error creating order: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/orders")
    public String getActiveOrders(Model model) {
        // Add active orders and tables (sample data, should be filled with real service)
        return "dashboard";
    }

    @GetMapping("/orders/by-table")
    public String getOrdersByTable(@RequestParam Integer tableNumber, Model model) {
        return null;
    }

    @GetMapping("/orders/{id}/details")
    public ResponseEntity<Order> getOrderDetails(@PathVariable Long id) {
        return null;
    }

    @GetMapping("/orders/all")
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
