package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.WishlistItem;

import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object for managing buyer wishlist items (Save-for-later).
 */
public interface WishlistDAO {

    /**
     * Adds a product to user's wishlist if not already present.
     * @return true if added, false if already exists
     */
    boolean addToWishlist(Long userId, Long productId) throws SQLException;

    /**
     * Removes a product from user's wishlist.
     * @return true if removed, false if not present
     */
    boolean removeFromWishlist(Long userId, Long productId) throws SQLException;

    /**
     * Checks if a product is in user's wishlist.
     */
    boolean isInWishlist(Long userId, Long productId) throws SQLException;

    /**
     * Retrieves all wishlist items for a given user, including active product details.
     */
    List<WishlistItem> findByUserId(Long userId) throws SQLException;

    /**
     * Returns total count of wishlist items for a user.
     */
    int getWishlistCount(Long userId) throws SQLException;
}
