package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.UserDAO;
import com.ribina.ribinamart.dto.UserResponseDTO;
import com.ribina.ribinamart.exception.AuthenticationException;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import com.ribina.ribinamart.util.PasswordUtil;
import com.ribina.ribinamart.util.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserResponseDTO register(String name, String email, String password, Role role) {
        Map<String, String> errors = new HashMap<>();

        if (!ValidationUtil.isNotEmpty(name) || name.trim().length() < 2 || name.trim().length() > 100) {
            errors.put("name", "Name must be between 2 and 100 characters.");
        }

        if (!ValidationUtil.isValidEmail(email)) {
            errors.put("email", "Please provide a valid email address.");
        }

        if (!ValidationUtil.isValidPassword(password)) {
            errors.put("password", "Password must be at least 6 characters long.");
        }

        if (role == null) {
            errors.put("role", "A valid role (BUYER or SELLER) must be selected.");
        } else if (role == Role.ADMIN) {
            errors.put("role", "Admin registration is restricted. Admin is a seeded account only.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Registration validation failed", errors);
        }

        try {
            if (userDAO.existsByEmail(email.trim().toLowerCase())) {
                errors.put("email", "An account with this email address already exists.");
                throw new ValidationException("Registration validation failed", errors);
            }

            User user = new User();
            user.setName(name.trim());
            user.setEmail(email.trim().toLowerCase());
            user.setPasswordHash(PasswordUtil.hashPassword(password));
            user.setRole(role);

            User saved = userDAO.save(user);
            return UserResponseDTO.fromEntity(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Database error during user registration", e);
        }
    }

    public UserResponseDTO login(String email, String password) {
        Map<String, String> errors = new HashMap<>();

        if (!ValidationUtil.isNotEmpty(email)) {
            errors.put("email", "Email is required.");
        }
        if (!ValidationUtil.isNotEmpty(password)) {
            errors.put("password", "Password is required.");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Login validation failed", errors);
        }

        try {
            User user = userDAO.findByEmail(email.trim().toLowerCase())
                    .orElseThrow(() -> new AuthenticationException("Invalid email or password."));

            if (!PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
                throw new AuthenticationException("Invalid email or password.");
            }

            return UserResponseDTO.fromEntity(user);
        } catch (SQLException e) {
            throw new RuntimeException("Database error during user login", e);
        }
    }

    public UserResponseDTO getUserById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        try {
            User user = userDAO.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
            return UserResponseDTO.fromEntity(user);
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving user", e);
        }
    }

    public List<UserResponseDTO> getAllUsers() {
        try {
            List<User> users = userDAO.findAll();
            List<UserResponseDTO> dtos = new ArrayList<>();
            for (User u : users) {
                dtos.add(UserResponseDTO.fromEntity(u));
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving all users", e);
        }
    }
}
