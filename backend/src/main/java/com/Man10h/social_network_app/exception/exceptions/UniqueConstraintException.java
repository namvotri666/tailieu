package com.Man10h.social_network_app.exception.exceptions;

public class UniqueConstraintException extends RuntimeException {
    public UniqueConstraintException(String message) {
        super(message);
    }
}
