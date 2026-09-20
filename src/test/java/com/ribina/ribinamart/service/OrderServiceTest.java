package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.CartDAO;
import com.ribina.ribinamart.dao.OrderDAO;
import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dto.OrderDTO;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

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
    public void testCheckoutEmptyCartThrowsValidationException() throws Exception {
        when(cartDAO.findByUserId(1L)).thenReturn(Collections.emptyList());

        assertThrows(ValidationException.class, () ->
                orderService.checkout(1L, "Room 202, Hostel B", "MOCK_CARD"));

        verify(orderDAO, never()).createOrderWithItems(any(Order.class), any());
    }

    @Test
    public void testCheckoutMissingAddressThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                orderService.checkout(1L, "", "MOCK_CARD"));
    }

    @Test
    public void testCheckoutSuccess() throws Exception {
        CartItem item = new CartItem(1L, 1L, 10L, 2, null);
        when(cartDAO.findByUserId(1L)).thenReturn(Collections.singletonList(item));

        Product product = new Product();
        product.setId(10L);
        product.setName("USB Cable");
        product.setPrice(new BigDecimal("150.00"));
        product.setStockQuantity(10);
        product.setStatus(ProductStatus.ACTIVE);

        when(productDAO.findById(10L)).thenReturn(Optional.of(product));

        when(orderDAO.createOrderWithItems(any(Order.class), any())).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(99L);
            return o;
        });

        OrderDTO orderDTO = orderService.checkout(1L, "Hostel C, Room 10", "MOCK_CARD");
        assertNotNull(orderDTO);
        assertEquals(99L, orderDTO.getId());
        assertEquals(new BigDecimal("300.00"), orderDTO.getTotalAmount());
    }
}
