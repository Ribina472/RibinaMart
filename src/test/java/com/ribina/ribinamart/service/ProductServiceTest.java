package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dao.ReviewDAO;
import com.ribina.ribinamart.dto.ProductDTO;
import com.ribina.ribinamart.exception.AuthorizationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductDAO productDAO;

    @Mock
    private ReviewDAO reviewDAO;

    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = new ProductService(productDAO, reviewDAO);
    }

    @Test
    public void testCreateProductSuccess() throws Exception {
        when(productDAO.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(201L);
            return p;
        });

        ProductDTO dto = productService.createProduct(1L, "Math Textbook", "Calculus Volume 1",
                new BigDecimal("450.00"), 15, "Books", "https://img.url");

        assertNotNull(dto);
        assertEquals(201L, dto.getId());
        assertEquals("Math Textbook", dto.getName());
        assertEquals(ProductStatus.ACTIVE, dto.getStatus());
    }

    @Test
    public void testCreateProductInvalidPrice() {
        assertThrows(ValidationException.class, () ->
                productService.createProduct(1L, "Bad Price Item", "Desc",
                        new BigDecimal("-10.00"), 5, "Books", null));
    }

    @Test
    public void testUpdateProductUnauthorizedSeller() throws Exception {
        Product existing = new Product();
        existing.setId(50L);
        existing.setSellerId(1L); // Owned by seller 1

        when(productDAO.findById(50L)).thenReturn(Optional.of(existing));

        // Seller 2 attempts to edit seller 1's product
        assertThrows(AuthorizationException.class, () ->
                productService.updateProduct(2L, 50L, "New Title", "Desc",
                        new BigDecimal("200.00"), 10, "Electronics", null));
    }
}
