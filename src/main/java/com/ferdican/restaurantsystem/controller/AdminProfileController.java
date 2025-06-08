package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.services.AdminProfileService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin-profile")
public class AdminProfileController {

    private final UsersRepository usersRepository;
    private final AdminProfileService adminProfileService;

    public AdminProfileController(UsersRepository usersRepository, AdminProfileService adminProfileService) {
        this.usersRepository = usersRepository;
        this.adminProfileService = adminProfileService;
    }




}
