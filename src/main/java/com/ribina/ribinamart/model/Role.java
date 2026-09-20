package com.ribina.ribinamart.model;

public enum Role {
    BUYER,
    SELLER,
    ADMIN;

    public static Role fromString(String roleStr) {
        if (roleStr == null) return null;
        for (Role r : values()) {
            if (r.name().equalsIgnoreCase(roleStr.trim())) {
                return r;
            }
        }
        return null;
    }
}
