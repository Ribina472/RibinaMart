package com.ribina.ribinamart.dao;

import com.ribina.ribinamart.model.Review;

import java.sql.SQLException;
import java.util.List;

public interface ReviewDAO {
    Review save(Review review) throws SQLException;
    List<Review> findByProductId(Long productId) throws SQLException;
    boolean hasUserPurchasedProduct(Long userId, Long productId) throws SQLException;
    double getAverageRating(Long productId) throws SQLException;
    int getReviewCount(Long productId) throws SQLException;
}
