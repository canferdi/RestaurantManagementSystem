package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Customer;
import com.ferdican.restaurantsystem.entity.Waiter;
import com.ferdican.restaurantsystem.repository.CustomerRepository;
import com.ferdican.restaurantsystem.repository.WaiterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WaiterService {

    private final WaiterRepository waiterRepository;

    @Autowired
    public WaiterService(WaiterRepository waiterRepository) {
        this.waiterRepository = waiterRepository;
    }

    public Optional<Waiter> getOne(Long id) {
        return waiterRepository.findById(id);
    }

    public Waiter addNew(Waiter waiter) {
        return waiterRepository.save(waiter);
    }


}
