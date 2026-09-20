package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDAOTest {

    private static UserDAO userDAO;
    private static ProductDAO productDAO;
    private static OrderDAO orderDAO;
    private static CartDAO cartDAO;

    private Long buyerId;
    private Long sellerId;
    private Long productId;

    @BeforeAll
    public static void init() throws Exception {
        TestDBHelper.setupTestDatabase();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
        orderDAO = new OrderDAOImpl();
        cartDAO = new CartDAOImpl();
    }

    @BeforeEach
    public void setup() throws Exception {
        TestDBHelper.clearTables();

        User buyer = new User(null, "Buyer 1", "buyer@test.com", "hash", Role.BUYER, null);
        buyerId = userDAO.save(buyer).getId();

        User seller = new User(null, "Seller 1", "seller@test.com", "hash", Role.SELLER, null);
        sellerId = userDAO.save(seller).getId();

        Product p = new Product(null, sellerId, "Desk Lamp", "LED Lamp", new BigDecimal("599.00"), 10, "Home", null, ProductStatus.ACTIVE, null);
        productId = productDAO.save(p).getId();

        cartDAO.addItem(buyerId, productId, 2);
    }

    @Test
    public void testCreateOrderWithItemsTransaction() throws Exception {
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setTotalAmount(new BigDecimal("1198.00"));
        order.setStatus(OrderStatus.CONFIRMED);
        order.setShippingAddress("Room 101, Campus Hostel");

        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(2);
        item.setPriceAtPurchase(new BigDecimal("599.00"));

        Order created = orderDAO.createOrderWithItems(order, Collections.singletonList(item));
        assertNotNull(created.getId());

        // Verify product stock was decremented from 10 to 8
        Product updatedProduct = productDAO.findById(productId).get();
        assertEquals(8, updatedProduct.getStockQuantity());

        // Verify cart was cleared
        List<CartItem> cartItems = cartDAO.findByUserId(buyerId);
        assertTrue(cartItems.isEmpty());

        // Verify finding by buyer
        List<Order> buyerOrders = orderDAO.findByBuyerId(buyerId);
        assertEquals(1, buyerOrders.size());
        assertEquals(OrderStatus.CONFIRMED, buyerOrders.get(0).getStatus());

        // Verify finding by seller
        List<Order> sellerOrders = orderDAO.findBySellerId(sellerId);
        assertEquals(1, sellerOrders.size());

        // Test status update
        boolean updated = orderDAO.updateStatus(created.getId(), OrderStatus.DELIVERED);
        assertTrue(updated);
        Optional<Order> reloaded = orderDAO.findById(created.getId());
        assertEquals(OrderStatus.DELIVERED, reloaded.get().getStatus());
    }
}
