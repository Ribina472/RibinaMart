package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dao.WishlistDAO;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WishlistServiceTest {

    @Mock
    private WishlistDAO wishlistDAO;

    @Mock
    private ProductDAO productDAO;

    private WishlistService wishlistService;

    @BeforeEach
    public void setUp() {
        wishlistService = new WishlistService(wishlistDAO, productDAO);
    }

    @Test
    public void testAddToWishlistSuccess() throws Exception {
        Product p = new Product();
        p.setId(10L);
        p.setStatus(ProductStatus.ACTIVE);

        when(productDAO.findById(10L)).thenReturn(Optional.of(p));
        when(wishlistDAO.addToWishlist(1L, 10L)).thenReturn(true);

        boolean result = wishlistService.addToWishlist(1L, 10L);
        assertTrue(result);
        verify(wishlistDAO, times(1)).addToWishlist(1L, 10L);
    }

    @Test
    public void testAddToWishlistInactiveProductThrowsValidationException() throws Exception {
        Product p = new Product();
        p.setId(10L);
        p.setStatus(ProductStatus.INACTIVE);

        when(productDAO.findById(10L)).thenReturn(Optional.of(p));

        assertThrows(ValidationException.class, () -> wishlistService.addToWishlist(1L, 10L));
        try {
            verify(wishlistDAO, never()).addToWishlist(anyLong(), anyLong());
        } catch (Exception ignored) {}
    }

    @Test
    public void testAddToWishlistNonExistentProductThrowsResourceNotFoundException() throws Exception {
        when(productDAO.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> wishlistService.addToWishlist(1L, 999L));
    }

    @Test
    public void testAddToWishlistNullParamsThrowsValidationException() {
        assertThrows(ValidationException.class, () -> wishlistService.addToWishlist(null, 10L));
        assertThrows(ValidationException.class, () -> wishlistService.addToWishlist(1L, null));
    }

    @Test
    public void testRemoveFromWishlist() throws Exception {
        when(wishlistDAO.removeFromWishlist(1L, 10L)).thenReturn(true);

        boolean result = wishlistService.removeFromWishlist(1L, 10L);
        assertTrue(result);
        verify(wishlistDAO).removeFromWishlist(1L, 10L);
    }

    @Test
    public void testIsInWishlist() throws Exception {
        when(wishlistDAO.isInWishlist(1L, 10L)).thenReturn(true);
        when(wishlistDAO.isInWishlist(1L, 20L)).thenReturn(false);

        assertTrue(wishlistService.isInWishlist(1L, 10L));
        assertFalse(wishlistService.isInWishlist(1L, 20L));
    }
}
