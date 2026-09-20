package com.ribina.ribinamart.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility for hashing and verifying passwords using BCrypt.
 * Strictly adheres to security requirements: never stores plaintext, never uses MD5/SHA1.
 */
public final class PasswordUtil {

    private static final int BCRYPT_LOG_ROUNDS = 12;

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Hashes a raw plaintext password using BCrypt with a work factor of 12.
     *
     * @param plainPassword the raw password to hash
     * @return the resulting salted BCrypt hash string
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_LOG_ROUNDS));
    }

    /**
     * Verifies a raw plaintext password against a stored BCrypt hash.
     *
     * @param plainPassword the raw password to check
     * @param hashedPassword the stored BCrypt hash
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Malformed hash
            return false;
        }
    }
}
