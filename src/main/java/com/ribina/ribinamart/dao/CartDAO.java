package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.CartItem;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CartDAO {
    List<CartItem> findByUserId(Long userId) throws SQLException;
    Optional<CartItem> findByUserAndProduct(Long userId, Long productId) throws SQLException;
    void addItem(Long userId, Long productId, int quantity) throws SQLException;
    boolean updateQuantity(Long cartItemId, Long userId, int quantity) throws SQLException;
    boolean removeItem(Long cartItemId, Long userId) throws SQLException;
    void clearCart(Long userId) throws SQLException;
    void clearCart(Long userId, Connection conn) throws SQLException;
}
