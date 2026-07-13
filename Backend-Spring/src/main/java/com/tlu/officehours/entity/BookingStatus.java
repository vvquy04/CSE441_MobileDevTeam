package com.tlu.officehours.entity;

/**
 * Enum for booking status - fixes the inconsistency in the Laravel version
 * where 'cancelled' and 'CANCELLED' were used interchangeably.
 */
public enum BookingStatus {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    REJECTED("rejected"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    private final String value;

    BookingStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Case-insensitive lookup to handle both 'cancelled' and 'CANCELLED' from existing data
     */
    public static BookingStatus fromValue(String value) {
        for (BookingStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown booking status: " + value);
    }
}
