package com.ribina.ribinamart.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validation utilities for rejecting invalid input at the service layer before reaching DAOs.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isValidStock(int stock) {
        return stock >= 0;
    }

    public static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
}
