package com.ribina.ribinamart.service;

import com.ribina.ribinamart.dao.ProductDAO;
import com.ribina.ribinamart.dao.ReviewDAO;
import com.ribina.ribinamart.dto.ReviewDTO;
import com.ribina.ribinamart.exception.ResourceNotFoundException;
import com.ribina.ribinamart.exception.ValidationException;
import com.ribina.ribinamart.model.Review;
import com.ribina.ribinamart.util.ValidationUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO;
    private final ProductDAO productDAO;

    public ReviewService(ReviewDAO reviewDAO, ProductDAO productDAO) {
        this.reviewDAO = reviewDAO;
        this.productDAO = productDAO;
    }

    public ReviewDTO addReview(Long buyerId, Long productId, int rating, String comment) {
        if (buyerId == null || buyerId <= 0) {
            throw new ValidationException("Invalid buyer ID.");
        }
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID.");
        }
        if (!ValidationUtil.isValidRating(rating)) {
            throw new ValidationException("Star rating must be between 1 and 5.");
        }

        try {
            productDAO.findById(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

            boolean purchased = reviewDAO.hasUserPurchasedProduct(buyerId, productId);
            if (!purchased) {
                throw new ValidationException("Only buyers who have purchased this product may submit a verified review.");
            }

            Review review = new Review();
            review.setBuyerId(buyerId);
            review.setProductId(productId);
            review.setRating(rating);
            review.setComment(comment != null ? comment.trim() : "");

            Review saved = reviewDAO.save(review);
            return ReviewDTO.fromEntity(saved);
        } catch (SQLException e) {
            throw new RuntimeException("Database error saving review", e);
        }
    }

    public List<ReviewDTO> getProductReviews(Long productId) {
        if (productId == null || productId <= 0) {
            throw new ValidationException("Invalid product ID.");
        }
        try {
            List<Review> reviews = reviewDAO.findByProductId(productId);
            List<ReviewDTO> dtos = new ArrayList<>();
            for (Review r : reviews) {
                dtos.add(ReviewDTO.fromEntity(r));
            }
            return dtos;
        } catch (SQLException e) {
            throw new RuntimeException("Database error retrieving reviews", e);
        }
    }

    public boolean canUserReview(Long buyerId, Long productId) {
        if (buyerId == null || productId == null) return false;
        try {
            return reviewDAO.hasUserPurchasedProduct(buyerId, productId);
        } catch (SQLException e) {
            return false;
        }
    }
}
