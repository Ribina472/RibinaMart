package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Product;
import com.ribina.ribinamart.model.WishlistItem;
import com.ribina.ribinamart.util.DBConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of WishlistDAO using HikariCP connection pooling and prepared statements.
 */
public class WishlistDAOImpl implements WishlistDAO {

    private static final Logger LOGGER = LoggerFactory.getLogger(WishlistDAOImpl.class);

    private static final String SQL_CHECK_EXISTS =
            "SELECT 1 FROM wishlist_items WHERE user_id = ? AND product_id = ?";

    private static final String SQL_INSERT =
            "INSERT INTO wishlist_items (user_id, product_id) VALUES (?, ?)";

    private static final String SQL_DELETE =
            "DELETE FROM wishlist_items WHERE user_id = ? AND product_id = ?";

    private static final String SQL_COUNT =
            "SELECT COUNT(*) FROM wishlist_items WHERE user_id = ?";

    private static final String SQL_FIND_BY_USER =
            "SELECT w.id, w.user_id, w.product_id, w.created_at, " +
            "p.name AS product_name, p.description AS product_desc, p.price AS product_price, " +
            "p.stock_quantity, p.image_url, p.category " +
            "FROM wishlist_items w " +
            "JOIN products p ON w.product_id = p.id " +
            "WHERE w.user_id = ? AND p.status = 'ACTIVE' " +
            "ORDER BY w.created_at DESC";

    @Override
    public boolean addToWishlist(Long userId, Long productId) throws SQLException {
        if (isInWishlist(userId, productId)) {
            return false;
        }

        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to add product {} to wishlist for user {}: {}", productId, userId, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean removeFromWishlist(Long userId, Long productId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Failed to remove product {} from wishlist for user {}: {}", productId, userId, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean isInWishlist(Long userId, Long productId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_CHECK_EXISTS)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public List<WishlistItem> findByUserId(Long userId) throws SQLException {
        List<WishlistItem> items = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_USER)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishlistItem item = new WishlistItem();
                    item.setId(rs.getLong("id"));
                    item.setUserId(rs.getLong("user_id"));
                    item.setProductId(rs.getLong("product_id"));
                    java.sql.Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) {
                        item.setCreatedAt(ts.toLocalDateTime());
                    }

                    Product product = new Product();
                    product.setId(rs.getLong("product_id"));
                    product.setName(rs.getString("product_name"));
                    product.setDescription(rs.getString("product_desc"));
                    product.setPrice(rs.getBigDecimal("product_price"));
                    product.setStockQuantity(rs.getInt("stock_quantity"));
                    product.setImageUrl(rs.getString("image_url"));
                    product.setCategory(rs.getString("category"));

                    item.setProduct(product);
                    items.add(item);
                }
            }
        }
        return items;
    }

    @Override
    public int getWishlistCount(Long userId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
