package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.AdminProfile;
import com.ferdican.restaurantsystem.entity.UserProfile;
import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.repository.AdminProfileRepository;
import com.ferdican.restaurantsystem.repository.UserProfileRepository;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final AdminProfileRepository adminProfileRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        AdminProfileRepository adminProfileRepository,
                        UserProfileRepository userProfileRepository) {
        this.usersRepository = usersRepository;
        this.adminProfileRepository = adminProfileRepository;
        this.userProfileRepository = userProfileRepository;
    }

    public Users addNew(Users users) {
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        Long userTypeId = users.getUserTypeId().getUserTypeId();
        Users savedUser = usersRepository.save(users);

        if (userTypeId == 1){
            adminProfileRepository.save(new AdminProfile(savedUser));
        } else if (userTypeId == 2) {
            userProfileRepository.save(new UserProfile(savedUser));
        }

        return savedUser;
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }


}
