package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dao.WishlistDAO;
import com.ribina.ribinamart.exception.AppException;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.ProductStatus;
import com.ribina.ribinamart.model.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Service encapsulating business logic for buyer wishlist operations.
 */
public class WishlistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishlistService.class);

    private final WishlistDAO wishlistDAO;
    private final ProductDAO productDAO;

    public WishlistService(WishlistDAO wishlistDAO, ProductDAO productDAO) {
        this.wishlistDAO = wishlistDAO;
        this.productDAO = productDAO;
    }

    /**
     * Adds an active product to buyer's wishlist.
     */
    public boolean addToWishlist(Long userId, Long productId) {
        if (userId == null || productId == null) {
            throw new ValidationException("User ID and Product ID are required.");
        }

        try {
            Product product = productDAO.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product with ID " + productId + " not found."));

            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new ValidationException("Only active products can be added to your wishlist.");
            }

            return wishlistDAO.addToWishlist(userId, productId);
        } catch (SQLException e) {
            LOGGER.error("SQL error adding product to wishlist: {}", e.getMessage());
            throw new AppException("Unable to add product to wishlist. Please try again.", e);
        }
    }

    /**
     * Removes a product from buyer's wishlist.
     */
    public boolean removeFromWishlist(Long userId, Long productId) {
        if (userId == null || productId == null) {
            throw new ValidationException("User ID and Product ID are required.");
        }

        try {
            return wishlistDAO.removeFromWishlist(userId, productId);
        } catch (SQLException e) {
            LOGGER.error("SQL error removing product from wishlist: {}", e.getMessage());
            throw new AppException("Unable to remove product from wishlist. Please try again.", e);
        }
    }

    /**
     * Retrieves all saved items in the buyer's wishlist.
     */
    public List<WishlistItem> getWishlist(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }

        try {
            return wishlistDAO.findByUserId(userId);
        } catch (SQLException e) {
            LOGGER.error("SQL error retrieving wishlist for user {}: {}", userId, e.getMessage());
            throw new AppException("Unable to load wishlist. Please try again.", e);
        }
    }

    /**
     * Checks if a product is already in the buyer's wishlist.
     */
    public boolean isInWishlist(Long userId, Long productId) {
        if (userId == null || productId == null) {
            return false;
        }

        try {
            return wishlistDAO.isInWishlist(userId, productId);
        } catch (SQLException e) {
            LOGGER.error("SQL error checking wishlist item existence: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the count of saved wishlist items.
     */
    public int getWishlistCount(Long userId) {
        if (userId == null) {
            return 0;
        }

        try {
            return wishlistDAO.getWishlistCount(userId);
        } catch (SQLException e) {
            LOGGER.error("SQL error counting wishlist items: {}", e.getMessage());
            return 0;
        }
    }
}
