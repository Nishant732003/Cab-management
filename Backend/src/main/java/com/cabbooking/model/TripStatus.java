package com.cabbooking.model;

/**
 * TripStatus enum representing the various states a trip can be in within the cab booking system.
 */
public enum TripStatus {
    /**
     * The trip has been created but not yet confirmed.
     */
    SCHEDULED,
    /**
     * The trip has been successfully booked by a customer and is waiting for a
     * driver to start.
     */
    CONFIRMED,
    /**
     * The driver has started the trip, and it is currently underway.
     */
    IN_PROGRESS,
    /**
     * The trip has been successfully completed by the driver.
     */
    COMPLETED,
    /**
     * The trip has been cancelled by either the customer or the driver.
     */
    CANCELLED
}
