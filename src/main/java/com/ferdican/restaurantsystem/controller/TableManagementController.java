package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.services.TableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TableManagementController {
    private final TableService tableService;

    public TableManagementController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/tables/layout")
    public String showTableLayout(Model model) {
        model.addAttribute("tables", tableService.getAllTables());
        return "tables/layout";
    }
} 