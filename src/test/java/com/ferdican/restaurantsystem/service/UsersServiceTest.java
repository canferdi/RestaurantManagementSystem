package com.ferdican.restaurantsystem.services;

import com.ferdican.restaurantsystem.entity.Users;
import com.ferdican.restaurantsystem.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

public class UsersServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UsersService usersService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserByEmail_UserExists() {
        Users user = new Users();
        user.setUserId(1L);
        user.setEmail("testuser@example.com");
        when(usersRepository.findByEmail("testuser@example.com")).thenReturn(Optional.of(user));
        Optional<Users> found = usersService.getUserByEmail("testuser@example.com");
        assertEquals("testuser@example.com", found.get().getEmail());
    }

    @Test
    public void testGetUserByEmail_UserDoesNotExist() {
        when(usersRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());
        Optional<Users> found = usersService.getUserByEmail("nonexistent@example.com");
        assertFalse(found.isPresent());
    }
}