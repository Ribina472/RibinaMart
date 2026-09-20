package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.CartDAO;
import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartDAO cartDAO;

    @Mock
    private ProductDAO productDAO;

    private CartService cartService;

    @BeforeEach
    public void setUp() {
        cartService = new CartService(cartDAO, productDAO);
    }

    @Test
    public void testAddToCartExceedingStockThrowsValidationException() throws Exception {
        Product p = new Product();
        p.setId(10L);
        p.setStockQuantity(3);
        p.setStatus(ProductStatus.ACTIVE);

        when(productDAO.findById(10L)).thenReturn(Optional.of(p));
        when(cartDAO.findByUserAndProduct(1L, 10L)).thenReturn(Optional.empty());

        // Attempt to add 5 when only 3 in stock
        assertThrows(ValidationException.class, () ->
                cartService.addToCart(1L, 10L, 5));

        verify(cartDAO, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    public void testAddToCartZeroQuantityThrowsValidationException() {
        assertThrows(ValidationException.class, () ->
                cartService.addToCart(1L, 10L, 0));
    }
}
