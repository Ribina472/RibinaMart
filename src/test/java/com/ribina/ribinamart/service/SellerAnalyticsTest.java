package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.CartDAO;
import com.ribina.ribinamart.dao.OrderDAO;
import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dto.SellerAnalyticsDTO;
import com.ribina.ribinamart.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SellerAnalyticsTest {

    @Mock
    private OrderDAO orderDAO;

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = new OrderService(orderDAO, cartDAO, productDAO);
    }

    @Test
    public void testGetSellerAnalyticsCalculation() throws Exception {
        Long sellerId = 55L;

        // Order 1: Delivered, 2 items
        Order o1 = new Order();
        o1.setId(101L);
        o1.setStatus(OrderStatus.DELIVERED);

        OrderItem i1 = new OrderItem();
        i1.setQuantity(2);
        i1.setPriceAtPurchase(new BigDecimal("500.00")); // 1000.00

        OrderItem i2 = new OrderItem();
        i2.setQuantity(1);
        i2.setPriceAtPurchase(new BigDecimal("250.00")); // 250.00

        o1.setItems(Arrays.asList(i1, i2));

        // Order 2: Cancelled (should be excluded from revenue and units sold)
        Order o2 = new Order();
        o2.setId(102L);
        o2.setStatus(OrderStatus.CANCELLED);

        OrderItem i3 = new OrderItem();
        i3.setQuantity(4);
        i3.setPriceAtPurchase(new BigDecimal("100.00"));

        o2.setItems(List.of(i3));

        when(orderDAO.findBySellerId(sellerId)).thenReturn(Arrays.asList(o1, o2));

        // Products: 1 active with stock 2 (low stock), 1 active with stock 50, 1 inactive with stock 0
        Product p1 = new Product();
        p1.setStatus(ProductStatus.ACTIVE);
        p1.setStockQuantity(2); // low stock <= 5

        Product p2 = new Product();
        p2.setStatus(ProductStatus.ACTIVE);
        p2.setStockQuantity(50);

        Product p3 = new Product();
        p3.setStatus(ProductStatus.INACTIVE);
        p3.setStockQuantity(0); // low stock <= 5

        when(productDAO.findBySellerId(sellerId)).thenReturn(Arrays.asList(p1, p2, p3));

        SellerAnalyticsDTO analytics = orderService.getSellerAnalytics(sellerId);

        assertNotNull(analytics);
        assertEquals(new BigDecimal("1250.00"), analytics.getTotalRevenue(), "Revenue should sum non-cancelled orders");
        assertEquals(1, analytics.getTotalOrdersCount(), "Orders count should exclude cancelled");
        assertEquals(3, analytics.getTotalUnitsSold(), "Units sold should be 2 + 1 = 3");
        assertEquals(2, analytics.getActiveListingsCount(), "Active listings count should be 2");
        assertEquals(2, analytics.getLowStockCount(), "Low stock count (stock <= 5) should be 2 (p1 and p3)");
    }

    @Test
    public void testGetSellerAnalyticsNullSellerIdReturnsZeros() {
        SellerAnalyticsDTO analytics = orderService.getSellerAnalytics(null);
        assertNotNull(analytics);
        assertEquals(BigDecimal.ZERO, analytics.getTotalRevenue());
        assertEquals(0, analytics.getTotalOrdersCount());
    }
}
