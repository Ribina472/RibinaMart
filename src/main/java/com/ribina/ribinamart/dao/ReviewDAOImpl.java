package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Review;
import com.ribina.ribinamart.util.DBConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAOImpl implements ReviewDAO {

    private static final String SQL_INSERT =
            "INSERT INTO reviews (buyer_id, product_id, order_id, rating, comment) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_FIND_BY_PRODUCT =
            "SELECT r.id, r.buyer_id, u.name AS buyer_name, r.product_id, p.name AS product_name, " +
            "r.order_id, r.rating, r.comment, r.created_at " +
            "FROM reviews r " +
            "JOIN users u ON r.buyer_id = u.id " +
            "JOIN products p ON r.product_id = p.id " +
            "WHERE r.product_id = ? ORDER BY r.created_at DESC";

    private static final String SQL_HAS_PURCHASED =
            "SELECT COUNT(1) FROM order_items oi " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.buyer_id = ? AND oi.product_id = ? AND o.status != 'CANCELLED'";

    private static final String SQL_AVG_RATING =
            "SELECT COALESCE(AVG(CAST(rating AS DOUBLE)), 0.0) FROM reviews WHERE product_id = ?";

    private static final String SQL_COUNT_REVIEWS =
            "SELECT COUNT(1) FROM reviews WHERE product_id = ?";

    @Override
    public Review save(Review review) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, review.getBuyerId());
            ps.setLong(2, review.getProductId());
            if (review.getOrderId() != null) {
                ps.setLong(3, review.getOrderId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setInt(4, review.getRating());
            ps.setString(5, review.getComment());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    review.setId(rs.getLong(1));
                }
            }
            return review;
        }
    }

    @Override
    public List<Review> findByProductId(Long productId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_PRODUCT)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getLong("id"));
                    r.setBuyerId(rs.getLong("buyer_id"));
                    r.setBuyerName(rs.getString("buyer_name"));
                    r.setProductId(rs.getLong("product_id"));
                    r.setProductName(rs.getString("product_name"));
                    long orderId = rs.getLong("order_id");
                    if (!rs.wasNull()) {
                        r.setOrderId(orderId);
                    }
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    reviews.add(r);
                }
            }
        }
        return reviews;
    }

    @Override
    public boolean hasUserPurchasedProduct(Long userId, Long productId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_HAS_PURCHASED)) {
            ps.setLong(1, userId);
            ps.setLong(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public double getAverageRating(Long productId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_AVG_RATING)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Math.round(rs.getDouble(1) * 10.0) / 10.0;
                }
            }
        }
        return 0.0;
    }

    @Override
    public int getReviewCount(Long productId) throws SQLException {
        try (Connection conn = DBConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_REVIEWS)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
