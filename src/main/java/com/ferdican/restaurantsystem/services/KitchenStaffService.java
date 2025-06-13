package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.KitchenStaff;
import com.ferdican.restaurantsystem.repository.KitchenStaffRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KitchenStaffService {

    KitchenStaffRepository kitchenStaffRepository;

    public KitchenStaffService(KitchenStaffRepository kitchenStaffRepository) {
        this.kitchenStaffRepository = kitchenStaffRepository;
    }

    public Optional<KitchenStaff> getOne(Long id) {
        return kitchenStaffRepository.findById(id);
    }

    public KitchenStaff addNew(KitchenStaff kitchenStuff) {
        return kitchenStaffRepository.save(kitchenStuff);
    }

}
