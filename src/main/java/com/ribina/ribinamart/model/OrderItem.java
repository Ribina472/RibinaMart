package com.ribina.ribinamart.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal priceAtPurchase;
    private Timestamp createdAt;

    // Populated fields for UI/DTO
    private String productName;
    private String productImageUrl;
    private Long sellerId;

    public OrderItem() {
    }

    public OrderItem(Long id, Long orderId, Long productId, int quantity, BigDecimal priceAtPurchase, Timestamp createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public BigDecimal getSubtotal() {
        if (priceAtPurchase == null) return BigDecimal.ZERO;
        return priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
    }
}
