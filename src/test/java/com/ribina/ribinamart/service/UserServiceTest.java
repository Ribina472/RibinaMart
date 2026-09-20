package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.UserDAO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AuthenticationException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import com.ribina.ribinamart.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDAO userDAO;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userDAO);
    }

    @Test
    public void testRegisterSuccess() throws Exception {
        when(userDAO.existsByEmail("student@test.com")).thenReturn(false);
        when(userDAO.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(101L);
            return u;
        });

        UserResponseDTO result = userService.register("Student User", "student@test.com", "Password@123", Role.BUYER);
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals("Student User", result.getName());
        assertEquals("student@test.com", result.getEmail());
        assertEquals(Role.BUYER, result.getRole());
    }

    @Test
    public void testRegisterAdminThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                userService.register("Admin Attempt", "admin@fake.com", "Password@123", Role.ADMIN));
    }

    @Test
    public void testRegisterInvalidEmail() {
        assertThrows(ValidationException.class, () ->
                userService.register("Invalid Email", "not-an-email", "Password@123", Role.BUYER));
    }

    @Test
    public void testRegisterShortPassword() {
        assertThrows(ValidationException.class, () ->
                userService.register("Short Pass", "user@test.com", "123", Role.BUYER));
    }

    @Test
    public void testLoginSuccess() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setName("Alice");
        user.setEmail("alice@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("Secret@123"));
        user.setRole(Role.BUYER);

        when(userDAO.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.login("alice@test.com", "Secret@123");
        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals("Alice", result.getName());
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setEmail("alice@test.com");
        user.setPasswordHash(PasswordUtil.hashPassword("Secret@123"));
        user.setRole(Role.BUYER);

        when(userDAO.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        assertThrows(AuthenticationException.class, () ->
                userService.login("alice@test.com", "WrongPassword"));
    }
}
