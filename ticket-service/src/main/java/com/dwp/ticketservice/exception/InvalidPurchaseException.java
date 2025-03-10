package com.dwp.ticketservice.exception;

/**
 * Exception thrown when a ticket purchase violates business rules and constraints
 *
 * This runtime exception is used to indicate that a purchase request
 * is invalid, such as exceeding ticket limits, invalid ticket types and input parameters.
 */
public class InvalidPurchaseException extends RuntimeException {

    /**
     * Constructs a new InvalidPurchaseException with the specified detail message.
     *
     * @param message the detail message that explains the exception
     */
    public InvalidPurchaseException(String message) {
        super(message);
    }
}
