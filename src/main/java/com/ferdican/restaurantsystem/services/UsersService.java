package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Admin;
import com.ferdican.restaurantsystem.entity.UserProfile;
import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.entity.Waiter;
import com.ferdican.restaurantsystem.entity.KitchenStaff;
import com.ferdican.restaurantsystem.repository.AdminRepository;
import com.ferdican.restaurantsystem.repository.UserProfileRepository;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import com.ferdican.restaurantsystem.repository.WaiterRepository;
import com.ferdican.restaurantsystem.repository.KitchenStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final AdminRepository adminRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final WaiterRepository waiterRepository;
    private final KitchenStaffRepository kitchenStaffRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository,
                        AdminRepository adminRepository,
                        UserProfileRepository userProfileRepository,
                        PasswordEncoder passwordEncoder,
                        WaiterRepository waiterRepository,
                        KitchenStaffRepository kitchenStaffRepository) {
        this.usersRepository = usersRepository;
        this.adminRepository = adminRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.waiterRepository = waiterRepository;
        this.kitchenStaffRepository = kitchenStaffRepository;
    }

    public Users addNew(Users users) {
        users.setActive(true);
        users.setRegistrationDate(new Date(System.currentTimeMillis()));
        users.setPassword(passwordEncoder.encode(users.getPassword()));
        Long userTypeId = users.getUserTypeId().getUserTypeId();
        Users savedUser = usersRepository.save(users);

        if (userTypeId == 1){
            adminRepository.save(new Admin(savedUser));
        } else if (userTypeId == 2) {
            userProfileRepository.save(new UserProfile(savedUser));
        } else if (userTypeId == 3) {
            waiterRepository.save(new Waiter(savedUser));
        } else if (userTypeId == 4) {
            kitchenStaffRepository.save(new KitchenStaff(savedUser));
        }

        return savedUser;
    }

    public Optional<Users> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Object getCurrentUserProfile() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users users = usersRepository.findByEmail(username).orElseThrow(() -> new
                    UsernameNotFoundException("Could not find user"));
            Long userId = users.getUserId();
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                Admin recruiterProfile = adminRepository.findById(userId).orElse(new Admin());
                return recruiterProfile;
            }
            // ToDo: Uncomment and implement the logic for UserProfile
            //else {
                //JobSeekerProfile jobSeekerProfile = jobSeekerProfileRepository.findById(userId).orElse(new JobSeekerProfile());
                //return jobSeekerProfile;
            //}
        }
        return null;
    }

    public Users getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            Users user = usersRepository.findByEmail(username).orElseThrow(() -> new
                    UsernameNotFoundException("Could not find user"));
            return user;
        }
        return null;
    }

}
