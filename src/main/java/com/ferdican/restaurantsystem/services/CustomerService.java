package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Customer;
import com.ferdican.restaurantsystem.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Optional<Customer> getOne(Long id) {
        return customerRepository.findById(id);
    }

    public Customer addNew(Customer customer) {
        return customerRepository.save(customer);
    }

}
