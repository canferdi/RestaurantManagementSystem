package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.AdminProfile;
import com.ferdican.restaurantsystem.repository.AdminProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminProfileService {

    private final AdminProfileRepository adminProfileRepository;

    public AdminProfileService(AdminProfileRepository adminProfileRepository) {
        this.adminProfileRepository = adminProfileRepository;
    }

    public Optional<AdminProfile> getOne(Long id) {
        return adminProfileRepository.findById(id);
    }

    public AdminProfile addNew(AdminProfile adminProfile) {
        return adminProfileRepository.save(adminProfile);
    }


}
