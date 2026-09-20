package com.ribina.ribinamart.exception;

import java.util.Collections;
import java.util.Map;

/**
 * Thrown when service-layer input validation fails (HTTP 400).
 */
public class ValidationException extends AppException {
    private final Map<String, String> errors;

    public ValidationException(String message) {
        super(message, 400);
        this.errors = Collections.emptyMap();
    }

    public ValidationException(String message, Map<String, String> errors) {
        super(message, 400);
        this.errors = errors != null ? errors : Collections.emptyMap();
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
