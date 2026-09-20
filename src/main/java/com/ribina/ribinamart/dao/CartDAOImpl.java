package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.CartItem;
import com.ribina.ribinamart.util.DBConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartDAOImpl implements CartDAO {

    private static final String SQL_FIND_BY_USER =
            "SELECT c.id, c.user_id, c.product_id, c.quantity, c.created_at, " +
            "p.name AS product_name, p.price AS product_price, p.image_url, p.stock_quantity AS available_stock " +
            "FROM cart_items c " +
            "JOIN products p ON c.product_id = p.id " +
            "WHERE c.user_id = ? AND p.status = 'ACTIVE' " +
            "ORDER BY c.created_at DESC";

    private static final String SQL_FIND_BY_USER_AND_PRODUCT =
            "SELECT id, user_id, product_id, quantity, created_at FROM cart_items WHERE user_id = ? AND product_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE_QTY_BY_ID =
            "UPDATE cart_items SET quantity = ? WHERE id = ? AND user_id = ?";

    private static final String SQL_INCREMENT_QTY =
            "UPDATE cart_items SET quantity = quantity + ? WHERE user_id = ? AND product_id = ?";

    private static final String SQL_DELETE_ITEM =
            "DELETE FROM cart_items WHERE id = ? AND user_id = ?";

    private static final String SQL_CLEAR_CART =
            "DELETE FROM cart_items WHERE user_id = ?";

    @Override
    public List<CartItem> findByUserId(Long userId) throws SQLException {
        List<CartItem> items = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductPrice(rs.getBigDecimal("product_price"));
                    item.setProductImageUrl(rs.getString("image_url"));
                    item.setAvailableStock(rs.getInt("available_stock"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public Optional<CartItem> findByUserAndProduct(Long userId, Long productId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER_AND_PRODUCT)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setCreatedAt(rs.getTimestamp("created_at"));
                    return Optional.of(item);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void addItem(Long userId, Long productId, int quantity) throws SQLException {
        Optional<CartItem> existing = findByUserAndProduct(userId, productId);
        if (existing.isPresent()) {
            try (Connection conn = DBConnectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL_INCREMENT_QTY)) {
                ps.setInt(1, quantity);
                ps.setLong(2, userId);
                ps.setLong(3, productId);
                ps.executeUpdate();
            }
        } else {
            try (Connection conn = DBConnectionPool.getConnection();
                 PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
                ps.setLong(1, userId);
                ps.setLong(2, productId);
                ps.setInt(3, quantity);
                ps.executeUpdate();
            }
        }
    }

    @Override
    public boolean updateQuantity(Long cartItemId, Long userId, int quantity) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_QTY_BY_ID)) {
            ps.setInt(1, quantity);
            ps.setLong(2, cartItemId);
            ps.setLong(3, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean removeItem(Long cartItemId, Long userId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE_ITEM)) {
            ps.setLong(1, cartItemId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public void clearCart(Long userId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection()) {
            clearCart(userId, conn);
        }
    }

    @Override
    public void clearCart(Long userId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CLEAR_CART)) {
            ps.setLong(1, userId);
            ps.executeUpdate();
        }
    }
}
