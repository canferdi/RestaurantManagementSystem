package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.UsersType;
import com.ferdican.restaurantsystem.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersTypeService {


    private final UserTypeRepository userTypeRepository;

    @Autowired
    public UsersTypeService(UserTypeRepository userTypeRepository) {
        this.userTypeRepository = userTypeRepository;
    }

    public List<UsersType> getAll(){
        return userTypeRepository.findAll();
    }


}
