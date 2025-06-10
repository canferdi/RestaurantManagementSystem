package com.ferdican.restaurantsystem.controller;

import com.ferdican.restaurantsystem.entity.Customer;
import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("customer_page")
public class CustomerProfileController {

    private final UsersRepository usersRepository;
    private final CustomerService customerService;

    @Autowired
    public CustomerProfileController(UsersRepository usersRepository, CustomerService customerService) {
        this.usersRepository = usersRepository;
        this.customerService = customerService;
    }

    @GetMapping("/")
    public String CustomerProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new
                    UsernameNotFoundException("Could not found user"));
            Optional<Customer> customerProfile = customerService.getOne(users.getUserId());

            if (!customerProfile.isEmpty()) {
                model.addAttribute("profile", customerProfile.get());
            }
        }
        return "redirect:/dashboard/";
    }



}
