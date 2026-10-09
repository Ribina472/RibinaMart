package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.model.Role;
import com.ribina.ribinamart.model.User;
import com.ribina.ribinamart.model.WishlistItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WishlistDAOTest {

    private static UserDAO userDAO;
    private static ProductDAO productDAO;
    private static WishlistDAO wishlistDAO;

    private Long buyerId;
    private Long productId1;
    private Long productId2;

    @BeforeAll
    public static void init() throws Exception {
        TestDBHelper.setupTestDatabase();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
        wishlistDAO = new WishlistDAOImpl();
    }

    @BeforeEach
    public void setup() throws Exception {
        TestDBHelper.clearTables();

        User buyer = new User(null, "Buyer One", "buyer1@test.com", "hash", Role.BUYER, null);
        buyerId = userDAO.save(buyer).getId();

        User seller = new User(null, "Seller One", "seller1@test.com", "hash", Role.SELLER, null);
        Long sellerId = userDAO.save(seller).getId();

        Product p1 = new Product(null, sellerId, "Wireless Mouse", "Ergonomic 2.4GHz", new BigDecimal("499.00"), 20, "Electronics", null, ProductStatus.ACTIVE, null);
        productId1 = productDAO.save(p1).getId();

        Product p2 = new Product(null, sellerId, "Water Bottle", "1L Stainless Steel", new BigDecimal("299.00"), 15, "Stationery", null, ProductStatus.ACTIVE, null);
        productId2 = productDAO.save(p2).getId();
    }

    @Test
    public void testAddToWishlistAndRetrieve() throws Exception {
        boolean added = wishlistDAO.addToWishlist(buyerId, productId1);
        assertTrue(added, "Should successfully add product to wishlist");

        List<WishlistItem> items = wishlistDAO.findByUserId(buyerId);
        assertEquals(1, items.size());
        assertEquals(productId1, items.get(0).getProductId());
        assertEquals("Wireless Mouse", items.get(0).getProduct().getName());
        assertEquals(new BigDecimal("499.00"), items.get(0).getProduct().getPrice());
    }

    @Test
    public void testDuplicateAddReturnsFalse() throws Exception {
        assertTrue(wishlistDAO.addToWishlist(buyerId, productId1));
        assertFalse(wishlistDAO.addToWishlist(buyerId, productId1), "Duplicate item should not be added again");

        assertEquals(1, wishlistDAO.getWishlistCount(buyerId));
    }

    @Test
    public void testIsInWishlist() throws Exception {
        assertFalse(wishlistDAO.isInWishlist(buyerId, productId1));
        wishlistDAO.addToWishlist(buyerId, productId1);
        assertTrue(wishlistDAO.isInWishlist(buyerId, productId1));
        assertFalse(wishlistDAO.isInWishlist(buyerId, productId2));
    }

    @Test
    public void testRemoveFromWishlist() throws Exception {
        wishlistDAO.addToWishlist(buyerId, productId1);
        wishlistDAO.addToWishlist(buyerId, productId2);
        assertEquals(2, wishlistDAO.getWishlistCount(buyerId));

        boolean removed = wishlistDAO.removeFromWishlist(buyerId, productId1);
        assertTrue(removed);
        assertEquals(1, wishlistDAO.getWishlistCount(buyerId));

        assertFalse(wishlistDAO.isInWishlist(buyerId, productId1));
        assertTrue(wishlistDAO.isInWishlist(buyerId, productId2));
    }
}
