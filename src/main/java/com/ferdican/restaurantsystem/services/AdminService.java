package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Admin;
import com.ferdican.restaurantsystem.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public Optional<Admin> getOne(Long id) {
        return adminRepository.findById(id);
    }

    public Admin addNew(Admin admin) {
        return adminRepository.save(admin);
    }


}
