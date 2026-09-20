package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    private static UserDAO userDAO;

    @BeforeAll
    public static void init() throws Exception {
        TestDBHelper.setupTestDatabase();
        userDAO = new UserDAOImpl();
    }

    @BeforeEach
    public void cleanup() throws Exception {
        TestDBHelper.clearTables();
    }

    @Test
    public void testSaveAndFindById() throws Exception {
        User user = new User();
        user.setName("Test Buyer");
        user.setEmail("buyer@test.com");
        user.setPasswordHash("hashed_secret");
        user.setRole(Role.BUYER);

        User saved = userDAO.save(user);
        assertNotNull(saved.getId());

        Optional<User> found = userDAO.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Buyer", found.get().getName());
        assertEquals("buyer@test.com", found.get().getEmail());
        assertEquals(Role.BUYER, found.get().getRole());
    }

    @Test
    public void testFindByEmail() throws Exception {
        User user = new User();
        user.setName("Test Seller");
        user.setEmail("seller@test.com");
        user.setPasswordHash("hashed_secret");
        user.setRole(Role.SELLER);

        userDAO.save(user);

        Optional<User> found = userDAO.findByEmail("seller@test.com");
        assertTrue(found.isPresent());
        assertEquals("Test Seller", found.get().getName());

        assertTrue(userDAO.existsByEmail("seller@test.com"));
        assertFalse(userDAO.existsByEmail("unknown@test.com"));
    }
}
