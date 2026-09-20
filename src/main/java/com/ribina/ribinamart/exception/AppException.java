package com.ribina.ribinamart.exception;

public class AppException extends RuntimeException {
    private final int statusCode;

    public AppException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public AppException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
