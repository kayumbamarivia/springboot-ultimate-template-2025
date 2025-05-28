package com.spring.fortress.vehicles.enums;

/**
 * Enum representing the status of a user account.
 *
 * @author Fortress Backend
 * @since 1.0
 */
public enum Account {
    /**
     * Account is active and fully operational.
     */
    ACTIVE,

    /**
     * Account is pending verification or activation.
     */
    PENDING,

    /**
     * Account is inactive and cannot be used.
     */
    INACTIVE
}