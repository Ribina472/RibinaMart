package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.CartDAO;
import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dto.CartItemDTO;
import com.ribina.ribinamart.dto.CartSummaryDTO;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.CartItem;
import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService(CartDAO cartDAO, ProductDAO productDAO) {
        this.cartDAO = cartDAO;
        this.productDAO = productDAO;
    }

    public void addToCart(Long userId, Long productId, int quantity) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID.");
        }
        if (quantity <= 0) {
            throw new ValidationException("Quantity must be at least 1.");
        }

        try {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new ValidationException("This product is currently unavailable.");
            }

            // Check existing cart quantity
            Optional<CartItem> existing = cartDAO.findByUserAndProduct(userId, productId);
            int newTotal = quantity + (existing.map(CartItem::getQuantity).orElse(0));

            if (newTotal > product.getStockQuantity()) {
                throw new ValidationException("Cannot add " + quantity + " more. Only " + product.getStockQuantity() + " available in stock.");
            }

            cartDAO.addItem(userId, productId, quantity);
        } catch (SQLException e) {
            throw new RuntimeException("Database error adding item to cart", e);
        }
    }

    public void updateItemQuantity(Long userId, Long cartItemId, int quantity) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        if (cartItemId == null || cartItemId <= 0) {
            throw new ValidationException("Invalid cart item ID.");
        }
        if (quantity <= 0) {
            removeItem(userId, cartItemId);
            return;
        }

        try {
            List<CartItem> items = cartDAO.findByUserId(userId);
            CartItem target = items.stream()
                    .filter(i -> i.getId().equals(cartItemId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Cart item not found."));

            Product product = productDAO.findById(target.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

            if (quantity > product.getStockQuantity()) {
                throw new ValidationException("Requested quantity exceeds available stock (" + product.getStockQuantity() + ").");
            }

            cartDAO.updateQuantity(cartItemId, userId, quantity);
        } catch (SQLException e) {
            throw new RuntimeException("Database error updating cart item", e);
        }
    }

    public void removeItem(Long userId, Long cartItemId) {
        if (userId == null || cartItemId == null) {
            throw new ValidationException("Invalid user or cart item ID.");
        }
        try {
            cartDAO.removeItem(cartItemId, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error removing cart item", e);
        }
    }

    public CartSummaryDTO getCartSummary(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ValidationException("Invalid user ID.");
        }
        try {
            List<CartItem> items = cartDAO.findByUserId(userId);
            List<CartItemDTO> dtos = new ArrayList<>();
            for (CartItem item : items) {
                dtos.add(CartItemDTO.fromEntity(item));
            }
            return new CartSummaryDTO(dtos);
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving cart", e);
        }
    }

    public void clearCart(Long userId) {
        if (userId == null) return;
        try {
            cartDAO.clearCart(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error clearing cart", e);
        }
    }
}
