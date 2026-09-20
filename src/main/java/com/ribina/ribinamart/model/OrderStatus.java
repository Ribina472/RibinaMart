package com.ribina.ribinamart.model;

public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public static OrderStatus fromString(String statusStr) {
        if (statusStr == null) return PENDING;
        for (OrderStatus s : values()) {
            if (s.name().equalsIgnoreCase(statusStr.trim())) {
                return s;
            }
        }
        return PENDING;
    }
}
