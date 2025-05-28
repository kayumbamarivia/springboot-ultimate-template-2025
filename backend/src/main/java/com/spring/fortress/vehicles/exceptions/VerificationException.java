package com.spring.fortress.vehicles.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when verification processes fail.
 * Maps to HTTP 400 Bad Request status.
 *
 * @author Fortress Backend
 * @since 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class VerificationException extends RuntimeException {

    /**
     * Constructs a new verification exception with the specified message.
     *
     * @param message the detail message
     */
    public VerificationException(String message) {
        super(message);
    }
}