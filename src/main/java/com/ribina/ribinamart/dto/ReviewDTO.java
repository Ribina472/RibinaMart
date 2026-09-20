package com.ribina.ribinamart.dto;

import com.ribina.ribinamart.model.Review;

import java.sql.Timestamp;

public class ReviewDTO {
    private Long id;
    private Long buyerId;
    private String buyerName;
    private Long productId;
    private String productName;
    private Long orderId;
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public ReviewDTO() {
    }

    public static ReviewDTO fromEntity(Review r) {
        if (r == null) return null;
        ReviewDTO dto = new ReviewDTO();
        dto.setId(r.getId());
        dto.setBuyerId(r.getBuyerId());
        dto.setBuyerName(r.getBuyerName());
        dto.setProductId(r.getProductId());
        dto.setProductName(r.getProductName());
        dto.setOrderId(r.getOrderId());
        dto.setRating(r.getRating());
        dto.setComment(r.getComment());
        dto.setCreatedAt(r.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
