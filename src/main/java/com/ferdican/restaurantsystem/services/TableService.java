package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.RestorantTable;
import com.ferdican.restaurantsystem.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableService {
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public List<RestorantTable> getAllTables() {
        return tableRepository.findAll();
    }

    public RestorantTable getTableById(Long id) {
        return tableRepository.findById(id).orElse(null);
    }

    public List<RestorantTable> getTablesByWaiter(Long waiterId) {
        return tableRepository.findByWaiter_UserAccountId(waiterId);
    }

    public RestorantTable save(RestorantTable table) {
        return tableRepository.save(table);
    }

    public void updateTablePosition(Long tableId, int posX, int posY) {
        RestorantTable table = tableRepository.findById(tableId).orElseThrow();
        table.setPosX(posX);
        table.setPosY(posY);
        tableRepository.save(table);
    }
} 