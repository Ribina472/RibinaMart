package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.CartItem;
import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CartDAOTest {

    private static UserDAO userDAO;
    private static ProductDAO productDAO;
    private static CartDAO cartDAO;

    private Long buyerId;
    private Long productId;

    @BeforeAll
    public static void init() throws Exception {
        TestDBHelper.setupTestDatabase();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
        cartDAO = new CartDAOImpl();
    }

    @BeforeEach
    public void setup() throws Exception {
        TestDBHelper.clearTables();

        User buyer = new User(null, "Buyer 1", "buyer@test.com", "hash", Role.BUYER, null);
        buyerId = userDAO.save(buyer).getId();

        User seller = new User(null, "Seller 1", "seller@test.com", "hash", Role.SELLER, null);
        Long sellerId = userDAO.save(seller).getId();

        Product p = new Product(null, sellerId, "Notebook", "A5 Ruled", new BigDecimal("80.00"), 50, "Stationery", null, ProductStatus.ACTIVE, null);
        productId = productDAO.save(p).getId();
    }

    @Test
    public void testAddAndGetCartItems() throws Exception {
        cartDAO.addItem(buyerId, productId, 2);

        List<CartItem> items = cartDAO.findByUserId(buyerId);
        assertEquals(1, items.size());
        assertEquals(2, items.get(0).getQuantity());
        assertEquals("Notebook", items.get(0).getProductName());

        // Test quantity increment when re-adding
        cartDAO.addItem(buyerId, productId, 3);
        items = cartDAO.findByUserId(buyerId);
        assertEquals(1, items.size());
        assertEquals(5, items.get(0).getQuantity());

        // Test removeItem
        cartDAO.removeItem(items.get(0).getId(), buyerId);
        items = cartDAO.findByUserId(buyerId);
        assertTrue(items.isEmpty());
    }
}
