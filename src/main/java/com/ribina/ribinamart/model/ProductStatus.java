package com.ribina.ribinamart.model;

public enum ProductStatus {
    ACTIVE,
    INACTIVE,
    MODERATED;

    public static ProductStatus fromString(String statusStr) {
        if (statusStr == null) return ACTIVE;
        for (ProductStatus s : values()) {
            if (s.name().equalsIgnoreCase(statusStr.trim())) {
                return s;
            }
        }
        return ACTIVE;
    }
}
